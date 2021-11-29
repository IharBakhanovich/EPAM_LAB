package com.epam.esm.dao.impl.jdbc;

import com.epam.esm.dao.UserDao;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.model.impl.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Profile("dev")
@Repository
public class JdbcUserDaoImpl implements UserDao {
    private static final String FIND_ALL_ENTITIES_SQL = "select u.id as userId, u.nickName as userNickName from user as u";
    private static final String FIND_ALL_ENTITIES_PAGINATION_SQL
            = "select u.id as userId, u.nickName as userNickName from user as u" +
            " WHERE u.id IN (select * from (select id from user order by id LIMIT ?, ?) as query1)";
    private static final String INSERT_ENTITY_SQL = "insert into user (nickName) values (?)";
    private static final String DELETE_ENTITY_BY_ID_SQL = "delete from user where id = ?";
    private static final String UPDATE_ENTITY_SQL = "update user set nickName = ? where id = ?";
    private static final String FIND_ENTITY_BY_ID_SQL
            = "select u.id as userId, u.nickName as userNickName from user as u where u.id = ?";
    private static final String FIND_ENTITY_BY_NAME_SQL
            = "select u.id as userId, u.nickName as userNickName from user as u where u.nickName = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RowMapper<User> userRowMapper;

    public JdbcUserDaoImpl() {
    }

    /**
     * The setter of the {@link JdbcTemplate}.
     *
     * @param jdbcTemplate is the {@link JdbcTemplate} to set.
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * The setter of the {@link RowMapper<User>}.
     *
     * @param userRowMapper is the {@link RowMapper<User>} to set.
     */
    public void setUserRowMapper(RowMapper<User> userRowMapper) {
        this.userRowMapper = userRowMapper;
    }

    /**
     * Saves {@link User} in the database.
     *
     * @param user is the {@link User} to save.
     * @throws DuplicateException if a SQLException with the state 23505 or the state 23000 is thrown.
     */
    @Override
    public void save(User user) {
        jdbcTemplate.update(INSERT_ENTITY_SQL, user.getNickName());
    }

    /**
     * Finds all {@link User} entity in the database.
     *
     * @return List of the {@link User} objects.
     */
    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(FIND_ALL_ENTITIES_SQL, userRowMapper);
    }

    /**
     * Finds all {@link User} entity in the database.
     *
     * @param pageNumber              is the offset query parameter.
     * @param amountEntitiesOnThePage is the limit query parameter.
     * @return List of the {@link User} objects.
     */
    @Override
    public List<User> findAllPagination(int pageNumber, int amountEntitiesOnThePage) {
        return jdbcTemplate.query(FIND_ALL_ENTITIES_PAGINATION_SQL, userRowMapper,
                pageNumber * amountEntitiesOnThePage, amountEntitiesOnThePage);
    }

    /**
     * Finds {@link Optional <User>} in the database by the id of the {@link User}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<User>}.
     */
    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query(FIND_ENTITY_BY_ID_SQL, userRowMapper, id).stream().findFirst();
    }

    /**
     * Updates the {@link User}.
     *
     * @param user is the value of the {@link User} to update.
     */
    @Override
    public void update(User user) {
        jdbcTemplate.update(UPDATE_ENTITY_SQL, user.getNickName(), user.getId());
    }

    /**
     * Deletes the {@link User} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_ENTITY_BY_ID_SQL, id);
    }

    /**
     * Finds {@link Optional<User>} in the database by the id of the {@link User}.
     *
     * @param nickName is the {@link long} to find.
     * @return {@link Optional<User>}.
     */
    @Override
    public Optional<User> findByName(String nickName) {
        return jdbcTemplate.query(FIND_ENTITY_BY_NAME_SQL, userRowMapper, nickName).stream().findFirst();
    }
}
