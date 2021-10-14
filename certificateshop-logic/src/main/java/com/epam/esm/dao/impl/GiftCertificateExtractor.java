package com.epam.esm.dao.impl;

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


@Component("certificateExtractor")
public class GiftCertificateExtractor implements ResultSetExtractor<List<GiftCertificate>> {

    @Autowired
    @Qualifier("certificateTagMapper")
    private RowMapper<CertificateTag> tagRowMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<GiftCertificate> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<GiftCertificate> giftCertificates = new ArrayList<>();

        while (resultSet.next()) {
            final long currentId = resultSet.getLong("certificateId");
            if (giftCertificates.stream().anyMatch(giftCertificate -> giftCertificate.getId() == currentId)) {
                if (resultSet.getLong("tagId") != 0) {
                    CertificateTag certificateTag = tagRowMapper.mapRow(resultSet, resultSet.getRow());
                    giftCertificates.stream()
                            .filter(giftCertificate -> giftCertificate.getId() == currentId)
                            .findAny().ifPresent(giftCertificate -> giftCertificate.getTags().add(certificateTag));
                }
            } else {
                List<CertificateTag> allTagsThisCertificate = new ArrayList<CertificateTag>();
                GiftCertificate giftCertificate = new GiftCertificate(
                        resultSet.getLong("certificateId"),
                        resultSet.getString("certificateName"),
                        resultSet.getString("certificateDescription"),
                        resultSet.getBigDecimal("certificatePrice"),
                        resultSet.getLong("certificateDuration"),
                        resultSet.getTimestamp("certificateCreateDate").toLocalDateTime(),
                        resultSet.getTimestamp("certificateLastUpdateDate").toLocalDateTime(),
                        allTagsThisCertificate
                );
                giftCertificates.add(giftCertificate);
                if(resultSet.getLong("tagId") != 0) {
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
