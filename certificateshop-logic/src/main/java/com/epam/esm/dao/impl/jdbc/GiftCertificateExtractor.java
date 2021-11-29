package com.epam.esm.dao.impl.jdbc;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extract {@link List<GiftCertificate>} from the {@link ResultSet}.
 */
@Component("certificateExtractor")
public class GiftCertificateExtractor implements ResultSetExtractor<List<GiftCertificate>> {

    @Autowired
    @Qualifier("certificateTagMapper")
    private RowMapper<CertificateTag> tagRowMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Extract {@link List<GiftCertificate>} from the {@link ResultSet}.
     *
     * @param resultSet is the {@link ResultSet} to map.
     * @return {@link List<GiftCertificate>}.
     * @throws SQLException        when something went wrong.
     * @throws DataAccessException when the datasource is not available.
     */
    @Override
    public List<GiftCertificate> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<GiftCertificate> giftCertificates = new ArrayList<>();

        while (resultSet.next()) {
            final long currentId = resultSet.getLong(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_ID);
            if (giftCertificates.stream().anyMatch(giftCertificate -> giftCertificate.getId() == currentId)) {
                if (resultSet.getLong(ColumnNames.TABLE_TAG_COLUMN_ID) != 0) {
                    CertificateTag certificateTag = tagRowMapper.mapRow(resultSet, resultSet.getRow());
                    giftCertificates.stream()
                            .filter(giftCertificate -> giftCertificate.getId() == currentId)
                            .findAny().ifPresent(giftCertificate -> giftCertificate.getTags().add(certificateTag));
                }
            } else {
                List<CertificateTag> allTagsThisCertificate = new ArrayList<CertificateTag>();
                GiftCertificate giftCertificate = new GiftCertificate(
                        resultSet.getLong(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_ID),
                        resultSet.getString(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_NAME),
                        resultSet.getString(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_DESCRIPTION),
                        resultSet.getBigDecimal(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_PRICE),
                        resultSet.getLong(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_DURATION),
                        resultSet.getTimestamp(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_CREATE_DATE).toLocalDateTime(),
                        resultSet.getTimestamp(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_LAST_UPDATE_DATE).toLocalDateTime(),
                        allTagsThisCertificate
                );
                giftCertificates.add(giftCertificate);
                if (resultSet.getLong(ColumnNames.TABLE_TAG_COLUMN_ID) != 0) {
                    CertificateTag certificateTag = tagRowMapper.mapRow(resultSet, resultSet.getRow());
                    giftCertificates.stream()
                            .filter(giftCertificate1 -> giftCertificate1.getId() == currentId)
                            .findAny().ifPresent(giftCertificate1 -> giftCertificate1.getTags()
                            .add(certificateTag));
                }
            }
        }
        return giftCertificates;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setTagRowMapper(RowMapper<CertificateTag> tagRowMapper) {
        this.tagRowMapper = tagRowMapper;
    }
}
