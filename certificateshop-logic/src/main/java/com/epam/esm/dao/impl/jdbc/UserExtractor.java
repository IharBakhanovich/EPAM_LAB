package com.epam.esm.dao.impl.jdbc;

import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extract {@link List<User>} from the {@link ResultSet}.
 */
@Component
public class UserExtractor implements ResultSetExtractor<List<User>> {
    @Autowired
    private CertificateInJsonMapper certificateInJsonMapper;

    public void setCertificateInJsonMapper(CertificateInJsonMapper certificateInJsonMapper) {
        this.certificateInJsonMapper = certificateInJsonMapper;
    }

    /**
     * Extract {@link List<User>} from the {@link ResultSet}.
     *
     * @param resultSet is the {@link ResultSet} to map.
     * @return {@link List<User>}.
     * @throws SQLException        when something went wrong.
     * @throws DataAccessException when the datasource is not available.
     */
    @Override
    public List<User> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<User> users = new ArrayList<>();

//        while (resultSet.next()) {
//            final long currentId = resultSet.getLong(ColumnNames.TABLE_USER_COLUMN_ID);
//            final long currentOrderId = resultSet.getLong(ColumnNames.TABLE_USERORDER_COLUMN_ID);
//            if (users.stream().anyMatch(user -> user.getId() == currentId)) {
//                User user = users.stream().filter(user1 -> user1.getId() == currentId).findAny().get();
//                if (user.getOrders().stream().anyMatch(order -> order.getId() == currentOrderId)) {
//                    addGiftCertificateToCorrespondingOrderOfUserOrders(resultSet, users, currentId, currentOrderId);
//                } else {
//                    createNewOrderFillItWithCertificateAndAddOrderToUser(resultSet, users, currentId);
//                }
//            } else {
//                createUserFillItWithOrderAndCertificateAndAddToUsers(resultSet, users, currentId, currentOrderId);
//            }
//        }
        return users;
    }

//    private void createUserFillItWithOrderAndCertificateAndAddToUsers(ResultSet resultSet, List<User> users, long currentId, long currentOrderId) throws SQLException {
//        List<Order> allOrdersThisUser = new ArrayList<Order>();
//        User user = createNewUserFromResultSetLine(resultSet, allOrdersThisUser);
//        if (resultSet.getLong(ColumnNames.TABLE_USERORDER_COLUMN_ID) != 0) {
//            Order newOrder = createNewOrderFromResultSetLine(resultSet);
//            user.getOrders().add(newOrder);
//            users.add(user);
//            addGiftCertificateToNewOrderOfUsersOrders(resultSet, users, currentId, currentOrderId, newOrder);
//        } else {
//            users.add(user);
//        }
//    }
//
//    private void createNewOrderFillItWithCertificateAndAddOrderToUser(ResultSet resultSet, List<User> users, long currentId) throws SQLException {
//        Order newOrder = createNewOrderFromResultSetLine(resultSet);
//        if (resultSet.getString(ColumnNames.TABLE_USERORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON) != null) {
//            GiftCertificate giftCertificate = certificateInJsonMapper.mapRow(resultSet, resultSet.getRow());
//            newOrder.getCertificates().add(giftCertificate);
//            users.stream().filter(user2 -> user2.getId() == currentId)
//                    .findAny().ifPresent(user2 -> user2.getOrders().add(newOrder));
//        }
//    }
//
//    private User createNewUserFromResultSetLine(ResultSet resultSet, List<Order> allOrdersThisUser) throws SQLException {
//        User user = new User(
//                resultSet.getLong(ColumnNames.TABLE_USER_COLUMN_ID),
//                resultSet.getString(ColumnNames.TABLE_USER_COLUMN_NICKNAME),
//                allOrdersThisUser
//        );
//        return user;
//    }
//
//    private Order createNewOrderFromResultSetLine(ResultSet resultSet) throws SQLException {
//        return new Order(
//                resultSet.getLong(ColumnNames.TABLE_USERORDER_COLUMN_ID),
//                new User(resultSet.getLong(ColumnNames.TABLE_USER_COLUMN_ID),
//                        resultSet.getString(ColumnNames.TABLE_USER_COLUMN_NICKNAME),
//                        new ArrayList<>()),
//                resultSet.getTimestamp(ColumnNames.TABLE_USERORDER_COLUMN_CREATE_DATE).toLocalDateTime(),
//                resultSet.getString(ColumnNames.TABLE_USERORDER_COLUMN_NAME),
//                new ArrayList<>()
//        );
//    }
//
//    private void addGiftCertificateToCorrespondingOrderOfUserOrders(ResultSet resultSet, List<User> users, long currentId, long currentOrderId) throws SQLException {
//        if (resultSet.getString(ColumnNames.TABLE_USERORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON) != null) {
//            GiftCertificate giftCertificate = certificateInJsonMapper.mapRow(resultSet, resultSet.getRow());
//            users.stream().filter(user2 -> user2.getId() == currentId)
//                    .findAny().ifPresent(user2 -> user2.getOrders()
//                            .stream()
//                            .filter(order -> order.getId() == currentOrderId)
//                            .findAny().ifPresent(order -> order.getCertificates().add(giftCertificate)));
//        }
//    }
//
//    private void addGiftCertificateToNewOrderOfUsersOrders(ResultSet resultSet, List<User> users, long currentId, long currentOrderId, Order newOrder) throws SQLException {
//        if (resultSet.getString(ColumnNames.TABLE_USERORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON) != null) {
//            GiftCertificate giftCertificate = certificateInJsonMapper.mapRow(resultSet, resultSet.getRow());
//            newOrder.getCertificates().add(giftCertificate);
//        }
//    }
}
