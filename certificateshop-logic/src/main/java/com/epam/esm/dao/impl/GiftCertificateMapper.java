package com.epam.esm.dao.impl;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("certificateMapper")
public class GiftCertificateMapper implements RowMapper<GiftCertificate>{
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
