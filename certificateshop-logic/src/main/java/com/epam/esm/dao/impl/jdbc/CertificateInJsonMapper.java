package com.epam.esm.dao.impl.jdbc;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * To map String that contains {@link GiftCertificate} in JSON to a {@link GiftCertificate} object.
 */
@Component
public class CertificateInJsonMapper implements RowMapper<GiftCertificate> {
    /**
     * Maps the row from {@link ResultSet} to the {@link CertificateTag}.
     *
     * @param resultSet is the {@link ResultSet} to map from.
     * @param i         is the row to map.
     * @return {@link CertificateTag}.
     * @throws SQLException if something went wrong.
     */
    @SneakyThrows
    @Override
    public GiftCertificate mapRow(ResultSet resultSet, int i) throws SQLException {
        Gson gson = new Gson();
        return gson.fromJson(
                resultSet.getString(ColumnNames.TABLE_USERORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON),
                GiftCertificate.class);
    }
}
