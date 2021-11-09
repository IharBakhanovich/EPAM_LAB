package com.epam.esm.dao.impl.jpa;

import com.epam.esm.dao.ListToSetConverter;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.impl.jdbc.UserExtractor;
import com.epam.esm.model.impl.User;
import com.epam.esm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The class that implements the {@link UserDao} interface.
 */
@Profile("dev_jpa")
@Repository
@Transactional
public class JpaUserDaoImpl implements UserDao {
    private static final String FIND_ENTITY_BY_ID_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId where u.id = ?";
    private static final String FIND_ENTITY_BY_NAME_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId where u.nickName = ?";
    private static final String FIND_ALL_ENTITIES_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId";
    private static final String FIND_ALL_ENTITIES_PAGINATION_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId" +
            " WHERE u.id IN (select * from (select id from user order by id LIMIT ?, ?) as query1)";
    private static final String INSERT_ENTITY_SQL = "insert into user (nickName) values (?)";
    private static final List<String> USER_HEADERS = Arrays.asList("userId", "userNickName", "userOrderId",
            "orderCreateDate", "orderName", "orderCertificate");
    private UserRepository userRepository;
    private EntityManager entityManager;
    private ListToSetConverter listToSetConverter;
    private UserExtractor userExtractor;

    @Autowired
    public JpaUserDaoImpl(UserRepository userRepository, EntityManager entityManager,
                          ListToSetConverter resultListToResultSetConverter, UserExtractor userExtractor) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.listToSetConverter = resultListToResultSetConverter;
        this.userExtractor = userExtractor;
    }

    /**
     * Saves {@link User} in the database.
     *
     * @param user is the {@link User} to save.
     */
    @Override
    public void save(User user) {
        entityManager
                .createNativeQuery(INSERT_ENTITY_SQL)
                .setParameter(1, user.getNickName())
                .executeUpdate();
    }

    /**
     * Finds all {@link User} entity in the database.
     *
     * @return List of the {@link User} objects.
     */
    @Override
    public List<User> findAll() {
        Query query = entityManager.createNativeQuery(FIND_ALL_ENTITIES_SQL);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        return getEntities(result);
    }

    /**
     * Finds {@link Optional <User>} in the database by the id of the {@link User}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<User>}.
     */
    @Override
    public Optional<User> findById(long id) {
        Query query = entityManager.createNativeQuery(FIND_ENTITY_BY_ID_SQL).setParameter(1, id);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        List<User> users = getEntities(result);
        return users.stream().findFirst();
    }

    private List<User> getEntities(List<List<Object>> result) {
        ResultSet resultSet;
        List<User> users = new ArrayList<>();
        try {
            resultSet = listToSetConverter.getResultSet(USER_HEADERS, result);
            users = userExtractor.extractData(resultSet);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return users;
    }

    private List<List<Object>> convertListOfArrayToListOfLists(List<Object[]> resultList) {
        List<List<Object>> result = new ArrayList();
        for (Object[] objects : resultList) {
            List<Object> listOfObjects = new ArrayList<>(Arrays.asList(objects));
            result.add(listOfObjects);
        }
        return result;
    }

    /**
     * Updates the {@link User}.
     *
     * @param user is the value of the {@link User} to update.
     */
    @Override
    public void update(User user) {

    }

    /**
     * Deletes the {@link User} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {

    }

    /**
     * Finds {@link Optional<User>} in the database by the id of the {@link User}.
     *
     * @param nickName is the {@link long} to find.
     * @return {@link Optional<User>}.
     */
    @Override
    public Optional<User> findByName(String nickName) {
        Query query = entityManager.createNativeQuery(FIND_ENTITY_BY_NAME_SQL).setParameter(1, nickName);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        List<User> users = getEntities(result);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return users.stream().findFirst();
    }

    /**
     * Finds all {@link User} entity in the database.
     *
     * @param pageNumber              is the pageNumber query parameter.
     * @param amountEntitiesOnThePage is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link User} objects.
     */
    @Override
    public List<User> findAllPagination(int pageNumber, int amountEntitiesOnThePage) {
//        return entityManager.createNativeQuery(FIND_ALL_ENTITIES_PAGINATION_SQL, User.class)
//                .setParameter(1, pageNumber * amountEntitiesOnThePage)
//                .setParameter(2, amountEntitiesOnThePage).getResultList();

        Query query = entityManager.createNativeQuery(FIND_ALL_ENTITIES_PAGINATION_SQL)
                .setParameter(1, pageNumber * amountEntitiesOnThePage)
                .setParameter(2, amountEntitiesOnThePage);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        return getEntities(result);
    }
}
