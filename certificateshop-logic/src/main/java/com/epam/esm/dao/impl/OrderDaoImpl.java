package com.epam.esm.dao.impl;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The class that implements the OrderDao interface.
 */
@Repository
public class OrderDaoImpl implements OrderDao {
    private static final String FIND_ALL_ENTITIES_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId";
    private static final String INSERT_ENTITY_SQL
            = "insert into userorder (userId, create_date, name) values (?, ?, ?)";
    private static final String INSERT_VALUES_IN_USERORDER_CERTIFICATE_TABLE_SQL
            = "insert into userorder_certificate (userOrderId, certificateId, certificateInJSON) values (?, ?, ?)";
    private static final String FIND_ENTITY_BY_ID_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId where uo.id = ?";
    private static final String UPDATE_ENTITY_SQL
            = "update userorder set userId = ?, create_date = ?, name = ? where id = ?";
    private static final String DELETE_ENTITY_BY_ID_SQL = "delete from userorder where id = ?";
    private static final String FIND_ENTITY_BY_NAME_SQL
            = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
            " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
            " from user as u" +
            " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
            " ON u.id = uo.userId where uo.name = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ResultSetExtractor<List<Order>> orderExtractor;

    public OrderDaoImpl() {
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
     * @param giftCertificate is the id of the {@link GiftCertificate} to save in JSON format.
     */
    @Override
    public void saveIdsInUserorder_certificateTable(long orderId, long certificateId, GiftCertificate giftCertificate) {
        Gson gson = new Gson();
        String certificateInJson = gson.toJson(giftCertificate);
        jdbcTemplate.update(INSERT_VALUES_IN_USERORDER_CERTIFICATE_TABLE_SQL, orderId, certificateId, certificateInJson);
    }
}
