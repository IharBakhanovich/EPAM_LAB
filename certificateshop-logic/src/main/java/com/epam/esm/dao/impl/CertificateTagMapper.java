package com.epam.esm.dao.impl;

import com.epam.esm.model.impl.CertificateTag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps values from {@link ResultSet} to {@link CertificateTag}.
 */
@Component("certificateTagMapper")
public class CertificateTagMapper implements RowMapper<CertificateTag> {

    /**
     * Maps the row from {@link ResultSet} to the {@link CertificateTag}.
     *
     * @param resultSet is the {@link ResultSet} to map from.
     * @param i         is the row to map.
     * @return {@link CertificateTag}.
     * @throws SQLException if something went wrong.
     */
    @Override
    public CertificateTag mapRow(ResultSet resultSet, int i) throws SQLException {
        return new CertificateTag(
                resultSet.getLong(ColumnNames.TABLE_TAG_COLUMN_ID),
                resultSet.getString(ColumnNames.TABLE_TAG_COLUMN_NAME)
        );
    }
}
