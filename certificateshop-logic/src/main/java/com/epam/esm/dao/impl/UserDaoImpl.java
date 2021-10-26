package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.model.impl.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    private static final String FIND_ALL_ENTITIES_SQL = "select user.id as userId, user.nickName as userNickName from user";
    private static final String FIND_ALL_USERS_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId, uoc.certificateInJSON as orderCertificate from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId";
    private static final String INSERT_ENTITY_SQL = "insert into user (nickName) values (?)";
    private static final String DELETE_ENTITY_BY_ID_SQL = "delete from user where id = ?";
    private static final String UPDATE_ENTITY_SQL = "update user set nickName = ? where id = ?";
    private static final String FIND_ENTITY_BY_ID_SQL
            = "select user.id as userId, user.nickName as userNickName from user where id = ?";
    private static final String FIND_ENTITY_BY_NAME_SQL
            = "select user.id as userId, user.nickName as userNickName from user where nickName = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RowMapper<User> userRowMapper;

    private UserDaoImpl() {
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
