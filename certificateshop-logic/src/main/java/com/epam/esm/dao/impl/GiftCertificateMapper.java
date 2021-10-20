package com.epam.esm.dao.impl;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * To map certificates withowt {@link CertificateTag}.
 */
@Component("certificateMapper")
public class GiftCertificateMapper implements RowMapper<GiftCertificate> {
    /**
     * Maps {@link ResultSet} to {@link GiftCertificate}.
     *
     * @param resultSet is the {@link ResultSet} to map.
     * @param i         is the row of the {@link ResultSet} to map.
     * @return {@link GiftCertificate}.
     * @throws SQLException if something went wrong.
     */
    @Override
    public GiftCertificate mapRow(ResultSet resultSet, int i) throws SQLException {
        return new GiftCertificate(
                resultSet.getLong(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_ID),
                resultSet.getString(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_NAME),
                resultSet.getString(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_DESCRIPTION),
                resultSet.getBigDecimal(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_PRICE),
                resultSet.getLong(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_DURATION),
                resultSet.getTimestamp(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_CREATE_DATE).toLocalDateTime(),
                resultSet.getTimestamp(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_LAST_UPDATE_DATE).toLocalDateTime(),
                null
        );
    }
}
