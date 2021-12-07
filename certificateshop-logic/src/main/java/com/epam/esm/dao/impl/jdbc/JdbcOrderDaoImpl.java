package com.epam.esm.dao.impl.jdbc;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The class that implements the OrderDao interface.
 */
@Profile("dev")
@Repository
public class JdbcOrderDaoImpl implements OrderDao {
    private static final String FIND_ALL_ENTITIES_SQL
            = "select u.id as userId, u.nickName as userNickName, u.password as userPassword, u.role as userRole, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId";
    private static final String FIND_ALL_ENTITIES_BY_USER_ID_SQL
            = "select u.id as userId, u.nickName as userNickName, u.password as userPassword, u.role as userRole, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId WHERE uo.userId = ?";
    private static final String FIND_ALL_ENTITIES_SQL_PAGINATION
            = "select u.id as userId, u.nickName as userNickName, u.password as userPassword, u.role as userRole, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId" +
            " WHERE uo.id IN (select * from (select id from userorder order by id LIMIT ?, ?) as query1)";
    private static final String INSERT_ENTITY_SQL
            = "insert into userorder (userId, create_date, name) values (?, ?, ?)";
    private static final String INSERT_VALUES_IN_USERORDER_CERTIFICATE_TABLE_SQL
            = "insert into userorder_certificate" +
            " (userOrderId, certificateId, certificateInJSON, certificatePrice) values (?, ?, ?, ?)";
    private static final String FIND_ENTITY_BY_ID_SQL
            = "select u.id as userId, u.nickName as userNickName, u.password as userPassword, u.role as userRole, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId where uo.id = ?";
    private static final String UPDATE_ENTITY_SQL
            = "update userorder set userId = ?, create_date = ?, name = ? where id = ?";
    private static final String DELETE_ENTITY_BY_ID_SQL = "delete from userorder where id = ?";
    private static final String FIND_ENTITY_BY_NAME_SQL
            = "select u.id as userId, u.nickName as userNickName, u.password as userPassword, u.role as userRole, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId where uo.name = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResultSetExtractor<List<Order>> orderExtractor;

    public JdbcOrderDaoImpl() {
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
     * The setter of the {@link ResultSetExtractor<List<Order>>}.
     *
     * @param orderExtractor is the {@link ResultSetExtractor<List<Order>>} to set.
     */
    public void setOrderExtractor(ResultSetExtractor<List<Order>> orderExtractor) {
        this.orderExtractor = orderExtractor;
    }

    /**
     * Saves {@link Order} in the database.
     *
     * @param order is the {@link Order} to save.
     */
    @Override
    public void save(Order order) {
        jdbcTemplate.update(INSERT_ENTITY_SQL,
                order.getUser().getId(),
                order.getCreateDate(),
                order.getName());
    }

    /**
     * Finds all {@link Order} entity in the database.
     *
     * @return List of the {@link Order} objects.
     */
    @Override
    public List<Order> findAll() {
        return jdbcTemplate.query(FIND_ALL_ENTITIES_SQL, orderExtractor);
    }

    /**
     * Finds all {@link Order} entity in the database.
     *
     * @param pageNumber is the pageNumber query parameter.
     * @param amountEntitiesOnThePage  is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link Order} objects.
     */
    @Override
    public List<Order> findAllPagination(int pageNumber, int amountEntitiesOnThePage) {
        return jdbcTemplate.query(FIND_ALL_ENTITIES_SQL_PAGINATION, orderExtractor,
                pageNumber*amountEntitiesOnThePage, amountEntitiesOnThePage);
    }

    /**
     * Finds {@link Optional <Order>} in the database by the id of the {@link Order}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<Order>}.
     */
    @Override
    public Optional<Order> findById(long id) {
        return jdbcTemplate.query(FIND_ENTITY_BY_ID_SQL, orderExtractor, id).stream().findFirst();
    }

    /**
     * Updates the {@link Order}.
     *
     * @param order is the value of the {@link Order} to update.
     */
    @Override
    public void update(Order order) {
        jdbcTemplate.update(UPDATE_ENTITY_SQL,
                order.getUser().getId(),
                order.getCreateDate(),
                order.getName());
    }

    /**
     * Removes the {@link Order} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_ENTITY_BY_ID_SQL, id);
    }

    /**
     * Finds {@link Optional<Order>} in the database by the id of the {@link Order}.
     *
     * @param name is the {@link long} to find.
     * @return {@link Optional<Order>}.
     */
    @Override
    public Optional<Order> findByName(String name) {
        return jdbcTemplate.query(FIND_ENTITY_BY_NAME_SQL, orderExtractor, name).stream().findFirst();
    }

    /**
     * Saves orderId, certificateId and giftCertificate in JSON in the 'userorder_certificate' table of the database.
     *
     * @param orderId         is the id of the {@link Order} to save.
     * @param certificateId   is the id of the {@link GiftCertificate} to save.
     * @param giftCertificate is the {@link GiftCertificate} to save in JSON format.
     * @param certificatePrice is the {@link BigDecimal} to save as a price of the {@param giftCertificate}.
     */
    @Override
    public void saveIdsInUserorder_certificateTable(long orderId, long certificateId,
                                                    GiftCertificate giftCertificate, BigDecimal certificatePrice) {
        Gson gson = new Gson();
        String certificateInJson = gson.toJson(giftCertificate);
        jdbcTemplate.update(INSERT_VALUES_IN_USERORDER_CERTIFICATE_TABLE_SQL,
                orderId, certificateId, certificateInJson, certificatePrice);
    }

    /**
     * Finds all {@link Order} entity in the database which belongs to the {@link User} with the ID equals {@param userId}.
     *
     * @param userId is the ID of the {@link User} which orders is to fetch.
     * @return List of the {@link Order} objects.
     */
    @Override
    public List<Order> findAllByUserId(long userId) {
        return jdbcTemplate.query(FIND_ALL_ENTITIES_BY_USER_ID_SQL, orderExtractor, userId);
    }
}
