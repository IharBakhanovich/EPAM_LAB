package com.epam.esm.dao.impl;

import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extract {@link List<Order>} from the {@link ResultSet}.
 */
@Component
public class OrderExtractor implements ResultSetExtractor<List<Order>> {
    @Autowired
    private RowMapper<User> userRowMapper;

    @Autowired
    private CertificateInJsonMapper certificateInJsonMapper;

    public void setCertificateInJsonMapper(CertificateInJsonMapper certificateInJsonMapper) {
        this.certificateInJsonMapper = certificateInJsonMapper;
    }

    public void setUserRowMapper(RowMapper<User> userRowMapper) {
        this.userRowMapper = userRowMapper;
    }

    /**
     * Extract {@link List<Order>} from the {@link ResultSet}.
     *
     * @param resultSet is the {@link ResultSet} to map.
     * @return {@link List<Order>}.
     * @throws SQLException        when something went wrong.
     * @throws DataAccessException when the datasource is not available.
     */
    @Override
    public List<Order> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Order> orders = new ArrayList<>();

        while (resultSet.next()) {
            final long currentId = resultSet.getLong(ColumnNames.TABLE_USERORDER_COLUMN_ID);
            if (currentId != 0) {
                if (orders.stream().anyMatch(order -> order.getId() == currentId)) {
                    if (resultSet.getString(ColumnNames.TABLE_USERORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON) != null) {
                        GiftCertificate giftCertificate = certificateInJsonMapper.mapRow(resultSet, resultSet.getRow());
                        orders.stream()
                                .filter(order -> order.getId() == currentId)
                                .findAny().ifPresent(order -> order.getCertificates().add(giftCertificate));
                    }
                } else {
                    List<GiftCertificate> allCertificatesThisOrder = new ArrayList<GiftCertificate>();
                    User userThisOrder = userRowMapper.mapRow(resultSet, resultSet.getRow());
                    Order newOrder = new Order(
                            resultSet.getLong(ColumnNames.TABLE_USERORDER_COLUMN_ID),
                            userThisOrder,
                            resultSet.getTimestamp(ColumnNames.TABLE_USERORDER_COLUMN_CREATE_DATE).toLocalDateTime(),
                            resultSet.getString(ColumnNames.TABLE_USERORDER_COLUMN_NAME),
                            allCertificatesThisOrder
                    );
                    orders.add(newOrder);
                    if (resultSet.getString(ColumnNames.TABLE_USERORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON) != null) {
                        GiftCertificate giftCertificate = certificateInJsonMapper.mapRow(resultSet, resultSet.getRow());
                        orders.stream()
                                .filter(order -> order.getId() == currentId)
                                .findAny().ifPresent(order -> order.getCertificates().add(giftCertificate));
                    }
                }
            }
        }
        return orders;
    }
}
