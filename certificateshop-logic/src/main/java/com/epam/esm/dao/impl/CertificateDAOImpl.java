package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.model.impl.GiftCertificate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Component("certificateDAO")
public class CertificateDAOImpl implements CertificateDAO {

    private static final Logger LOGGER = LogManager.getLogger(CertificateDAOImpl.class);

    private static final String FIND_ALL_ENTITIES_SQL
            = "select c.id as certificateId, c.name as certificateName," +
            " c.description as certificateDescription, c.duration as certificateDuration," +
            " c.create_date as certificateCreateDate, c.price as certificatePrice," +
            " c.last_update_date as certificateLastUpdateDate, t.id as tagId, t.name as tagName" +
            " from gift_certificate as c LEFT OUTER JOIN (has_tag as h LEFT OUTER JOIN tag as t ON t.id = h.tagId)" +
            " ON c.id = h.certificateId";
    private static final String INSERT_ENTITY_SQL
            = "insert into gift_certificate (name, description, price, duration, create_date, last_update_date)" +
            " values (?, ?, ?, ?, ?, ?)";
    private static final String DELETE_ENTITY_BY_ID_SQL = "delete from gift_certificate where id = ?";
    private static final String UPDATE_ENTITY_SQL
            = "update gift_certificate" +
            " set name = ?, description = ?, price = ?, duration = ?, create_date = ?, last_update_date = ?" +
            " where id = ?";
    private static final String FIND_ENTITY_BY_ID_SQL
            = "select c.id as certificateId, c.name as certificateName," +
            " c.description as certificateDescription, c.duration as certificateDuration," +
            " c.create_date as certificateCreateDate, c.price as certificatePrice," +
            " c.last_update_date as certificateLastUpdateDate, t.id as tagId, t.name as tagName" +
            " from gift_certificate as c LEFT OUTER JOIN (has_tag as h LEFT OUTER JOIN tag as t ON t.id = h.tagId)" +
            " ON c.id = h.certificateId where c.id = ?";
    private static final String FIND_ENTITY_BY_NAME_SQL
            = "select c.id as certificateId, c.name as certificateName," +
            " c.description as certificateDescription, c.duration as certificateDuration," +
            " c.create_date as certificateCreateDate, c.price as certificatePrice," +
            " c.last_update_date as certificateLastUpdateDate, t.id as tagId, t.name as tagName" +
            " from gift_certificate as c LEFT OUTER JOIN (has_tag as h LEFT OUTER JOIN tag as t ON t.id = h.tagId)" +
            " ON c.id = h.certificateId where c.name = ?";
    private static final String INSERT_VALUES_IN_HAS_TAG_TABLE_SQL = "insert into has_tag (certificateId, tagId) values (?, ?)";
    private static final String DELETE_VALUES_IN_HAS_TAG_TABLE_SQL = "delete from has_tag where certificateId = ? and tagId = ?";
    private static final String FIND_CERTIFICATE_WITHOUT_TAGS_BY_NAME
            = "select c.id as certificateId, c.name as certificateName," +
            " c.description as certificateDescription, c.duration as certificateDuration," +
            " c.create_date as certificateCreateDate, c.price as certificatePrice," +
            " c.last_update_date as certificateLastUpdateDate from gift_certificate as c where c.name = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("certificateExtractor")
    private ResultSetExtractor<List<GiftCertificate>> giftCertificateExtractor;

    @Autowired
    @Qualifier("certificateMapper")
    private RowMapper<GiftCertificate> certificateMapper;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setGiftCertificateExtractor(ResultSetExtractor<List<GiftCertificate>> giftCertificateExtractor) {
        this.giftCertificateExtractor = giftCertificateExtractor;
    }

    private CertificateDAOImpl() {
    }

    @Override
    public List<GiftCertificate> findAll() {
        return jdbcTemplate.query(FIND_ALL_ENTITIES_SQL, giftCertificateExtractor);
    }


    @Override
    public Optional<GiftCertificate> findById(long id) {
        return jdbcTemplate.query(FIND_ENTITY_BY_ID_SQL, giftCertificateExtractor, id).stream().findFirst();
    }

    @Override
    public void save(GiftCertificate giftCertificate) {
        jdbcTemplate.update(INSERT_ENTITY_SQL,
                giftCertificate.getName(),
                giftCertificate.getDescription(),
                giftCertificate.getPrice(),
                giftCertificate.getDuration(),
                giftCertificate.getCreateDate(),
                giftCertificate.getLastUpdateDate());
    }

    @Override
    public void update(GiftCertificate giftCertificate) {
        jdbcTemplate.update(UPDATE_ENTITY_SQL,
                giftCertificate.getName(),
                giftCertificate.getDescription(),
                giftCertificate.getPrice(),
                giftCertificate.getDuration(),
                giftCertificate.getCreateDate(),
                giftCertificate.getLastUpdateDate(),
                giftCertificate.getId());
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_ENTITY_BY_ID_SQL, id);
    }

    @Override
    public Optional<GiftCertificate> findByName(String name) {
        return jdbcTemplate.query(FIND_ENTITY_BY_NAME_SQL, giftCertificateExtractor, name).stream().findFirst();
    }

    /**
     * Saves certificateId and tagId in the database.
     *
     * @param certificateId is the id of the {@link GiftCertificate} to save.
     * @param tagId is the id of the {@link com.epam.esm.model.impl.CertificateTag} to save
     *
     */
    @Override
    public void saveIdsInHas_tagTable(long certificateId, long tagId) {
        jdbcTemplate.update(INSERT_VALUES_IN_HAS_TAG_TABLE_SQL, certificateId, tagId);
    }

    @Override
    public void deleteIdsInHas_TagTable(long certificateId, Long tagId) {
        jdbcTemplate.update(DELETE_VALUES_IN_HAS_TAG_TABLE_SQL, certificateId, tagId);
    }

    @Override
    public Optional<GiftCertificate> findCertificateWithoutTagsByName(String name) {
        return jdbcTemplate.query(FIND_CERTIFICATE_WITHOUT_TAGS_BY_NAME, certificateMapper, name).stream().findFirst();
    }
}