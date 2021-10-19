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
     * @param resultSet is the {@link ResultSet} to map.
     * @param i is the row of the {@link ResultSet} to map.
     * @return {@link GiftCertificate}.
     * @throws SQLException if something went wrong.
     */
    @Override
    public GiftCertificate mapRow(ResultSet resultSet, int i) throws SQLException {
        return new GiftCertificate(
                resultSet.getLong("certificateId"),
                resultSet.getString("certificateName"),
                resultSet.getString("certificateDescription"),
                resultSet.getBigDecimal("certificatePrice"),
                resultSet.getLong("certificateDuration"),
                resultSet.getTimestamp("certificateCreateDate").toLocalDateTime(),
                resultSet.getTimestamp("certificateLastUpdateDate").toLocalDateTime(),
                null
        );
    }
}
