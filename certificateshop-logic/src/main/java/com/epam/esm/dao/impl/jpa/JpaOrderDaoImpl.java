package com.epam.esm.dao.impl.jpa;

import com.epam.esm.dao.ListToSetConverter;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.dao.impl.jdbc.OrderExtractor;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.repository.OrderRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The class that implements the {@link OrderDao} interface.
 */
@Profile("dev_jpa")
@Repository
@Transactional
public class JpaOrderDaoImpl implements OrderDao {
    private static final String INSERT_ENTITY_SQL
            = "insert into userorder (userId, create_date, name) values (?, ?, ?)";
    private static final String FIND_ENTITY_BY_NAME_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId where uo.name = ?";
    private static final String FIND_ENTITY_BY_ID_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId where uo.id = ?";
    private static final String FIND_ALL_ENTITIES_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId";
    private static final String FIND_ALL_ENTITIES_SQL_PAGINATION
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId" +
            " WHERE uo.id IN (select * from (select id from userorder order by id LIMIT ?, ?) as query1)";
    private static final String INSERT_VALUES_IN_USERORDER_CERTIFICATE_TABLE_SQL
            = "insert into userorder_certificate" +
            " (userOrderId, certificateId, certificateInJSON, certificatePrice) values (?, ?, ?, ?)";
    private static final List<String> ORDER_HEADERS = Arrays.asList(ColumnNames.TABLE_USER_COLUMN_ID,
            ColumnNames.TABLE_USER_COLUMN_NICKNAME, ColumnNames.TABLE_USERORDER_COLUMN_ID,
            ColumnNames.TABLE_USERORDER_COLUMN_CREATE_DATE, ColumnNames.TABLE_USERORDER_COLUMN_NAME,
            ColumnNames.TABLE_USERORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON);
    private OrderRepository orderRepository;
    private EntityManager entityManager;
    private ListToSetConverter listToSetConverter;
    private OrderExtractor orderExtractor;

    @Autowired
    public JpaOrderDaoImpl(OrderRepository orderRepository, EntityManager entityManager,
                           ListToSetConverter listToSetConverter, OrderExtractor orderExtractor) {
        this.orderRepository = orderRepository;
        this.entityManager = entityManager;
        this.listToSetConverter = listToSetConverter;
        this.orderExtractor = orderExtractor;
    }

    /**
     * Saves {@link Order} in the database.
     *
     * @param order is the {@link Order} to save.
     */
    @Override
    public void save(Order order) {
        entityManager
                .createNativeQuery(INSERT_ENTITY_SQL)
                .setParameter(1, order.getUser().getId())
                .setParameter(2, order.getCreateDate())
                .setParameter(3, order.getName())
                .executeUpdate();
    }

    /**
     * Finds all {@link Order} entity in the database.
     *
     * @return List of the {@link Order} objects.
     */
    @Override
    public List<Order> findAll() {
        Query query = entityManager.createNativeQuery(FIND_ALL_ENTITIES_SQL);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        return getEntities(result);
    }

    /**
     * Finds {@link Optional <Order>} in the database by the id of the {@link Order}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<Order>}.
     */
    @Override
    public Optional<Order> findById(long id) {
        Query query = entityManager.createNativeQuery(FIND_ENTITY_BY_ID_SQL).setParameter(1, id);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        List<Order> orders = getEntities(result);
        return orders.stream().findFirst();
    }

    /**
     * Updates the {@link Order}.
     *
     * @param order is the value of the {@link Order} to update.
     */
    @Override
    public void update(Order order) {
        Order orderFromDB = entityManager.find(Order.class, order.getId());
        entityManager.createQuery("UPDATE userorder o set o.name = :name, o.user = :user where o.id = :id")
                .setParameter("name", order.getName())
                .setParameter("user", order.getUser())
                .setParameter("id", order.getId()).executeUpdate();
        entityManager.refresh(orderFromDB);
    }

    /**
     * Removes the {@link Order} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {
        entityManager
                .createQuery("delete from userorder o where o.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    /**
     * Finds {@link Optional<Order>} in the database by the id of the {@link Order}.
     *
     * @param name is the {@link String} to find.
     * @return {@link Optional<Order>}.
     */
    @Override
    public Optional<Order> findByName(String name) {
        Query query = entityManager.createNativeQuery(FIND_ENTITY_BY_NAME_SQL).setParameter(1, name);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        List<Order> users = getEntities(result);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return users.stream().findFirst();
    }

    private List<List<Object>> convertListOfArrayToListOfLists(List<Object[]> resultList) {
        List<List<Object>> result = new ArrayList();
        for (Object[] objects : resultList) {
            List<Object> listOfObjects = new ArrayList<>(Arrays.asList(objects));
            result.add(listOfObjects);
        }
        return result;
    }

    private List<Order> getEntities(List<List<Object>> result) {
        ResultSet resultSet;
        List<Order> orders = new ArrayList<>();
        try {
            resultSet = listToSetConverter.getResultSet(ORDER_HEADERS, result);
            orders = orderExtractor.extractData(resultSet);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return orders;
    }

    /**
     * Saves orderId, certificateId and giftCertificate in JSON in the 'userorder_certificate' table of the database.
     *
     * @param orderId          is the id of the {@link Order} to save.
     * @param certificateId    is the id of the {@link GiftCertificate} to save.
     * @param giftCertificate  is the {@link GiftCertificate} to save in JSON format.
     * @param certificatePrice is the {@link BigDecimal} to save as a price of the {@param giftCertificate}.
     */
    @Override
    public void saveIdsInUserorder_certificateTable(long orderId, long certificateId, GiftCertificate giftCertificate,
                                                    BigDecimal certificatePrice) {
        Gson gson = new Gson();
        String certificateInJson = gson.toJson(giftCertificate);
        entityManager
                .createNativeQuery(INSERT_VALUES_IN_USERORDER_CERTIFICATE_TABLE_SQL)
                .setParameter(1, orderId)
                .setParameter(2, certificateId)
                .setParameter(3, certificateInJson)
                .setParameter(4, certificatePrice)
                .executeUpdate();
    }

    /**
     * Finds all {@link Order} entity in the database.
     *
     * @param pageNumber              is the pageNumber query parameter.
     * @param amountEntitiesOnThePage is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link Order} objects.
     */
    @Override
    public List<Order> findAllPagination(int pageNumber, int amountEntitiesOnThePage) {
        Query query = entityManager.createNativeQuery(FIND_ALL_ENTITIES_SQL_PAGINATION)
                .setParameter(1, pageNumber * amountEntitiesOnThePage)
                .setParameter(2, amountEntitiesOnThePage);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        return getEntities(result);
    }
}
