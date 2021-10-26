package com.epam.esm.dao.impl;

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
//        List<User> users = new ArrayList<>();
//
//        while (resultSet.next()) {
//            final long currentId = resultSet.getLong(ColumnNames.TABLE_USER_COLUMN_ID);
//            final long currentOrderId = resultSet.getLong(ColumnNames.TABLE_USERORDER_COLUMN_ID);
//            if (users.stream().anyMatch(user -> user.getId() == currentId)) {
//                User user = users.stream().filter(user1 -> user1.getId() == currentId).findAny().get();
//                if (user.getOrders().stream()
//                        .anyMatch(order -> order.getId() == currentOrderId)) {
//                    if (resultSet.getString(ColumnNames.TABLE_USER_ORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON) != null) {
//                        GiftCertificate giftCertificate = certificateInJsonMapper.mapRow(resultSet, resultSet.getRow());
//                        users.stream()
//                                .filter(user2 -> user2.getId() == currentId)
//                                .findAny().ifPresent(user2 -> user2.getOrders()
//                                        .stream()
//                                        .filter(order -> order.getId() == currentOrderId)
//                                        .findAny().ifPresent(order -> order.getCertificates().add(giftCertificate)));
//                    }
//                } else {
//                    Order newOrder = new Order(
//                            resultSet.getLong(ColumnNames.TABLE_USERORDER_COLUMN_ID),
//
//                    )
//
//                }
//
//            } else {
//                List<Order> allOrdersThisUser = new ArrayList<Order>();
//                User user = new User(
//                        resultSet.getLong(ColumnNames.TABLE_USER_COLUMN_ID),
//                        resultSet.getString(ColumnNames.TABLE_USER_COLUMN_NICKNAME),
//                        allOrdersThisUser
//                );
//                users.add(user);
//                if (resultSet.getString(ColumnNames.TABLE_USER_ORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON) != null) {
//                    GiftCertificate giftCertificate = certificateInJsonMapper.mapRow(resultSet, resultSet.getRow());
//                    users.stream()
//                            .filter(user1 -> user1.getId() == currentId)
//                            .findAny().ifPresent(user1 -> user1.getOrders()
//                                    .stream()
//                                    .filter(order -> order.getId() == currentOrderId)
//                                    .findAny().ifPresent(order -> order.getCertificates().add(giftCertificate)));
//                }
//            }
//        }
        return new ArrayList<>();
    }

}
