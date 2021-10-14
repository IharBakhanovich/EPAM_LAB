package com.epam.esm.dao.impl;

import com.epam.esm.model.impl.CertificateTag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("certificateTagMapper")
public class CertificateTagMapper implements RowMapper<CertificateTag> {

    @Override
    public CertificateTag mapRow(ResultSet resultSet, int i) throws SQLException {
        return new CertificateTag(
                resultSet.getLong("tagId"),
                resultSet.getString("tagName")
        );
    }
}