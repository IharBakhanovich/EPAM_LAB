package com.epam.esm.dao.impl.jdbc;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * The class that implements the CertificateDAO interface.
 */
@Profile("dev")
@Repository("certificateDAO")
@Component("certificateDAO")
public class JdbcCertificateDaoImpl implements CertificateDao {

    private static final Logger LOGGER = LogManager.getLogger(JdbcCertificateDaoImpl.class);

    private static final String FIND_ALL_ENTITIES_SQL_PAGINATION
            = "select c.id as certificateId, c.name as certificateName," +
            " c.description as certificateDescription, c.duration as certificateDuration," +
            " c.create_date as certificateCreateDate, c.price as certificatePrice," +
            " c.last_update_date as certificateLastUpdateDate, t.id as tagId, t.name as tagName" +
            " from gift_certificate as c LEFT OUTER JOIN (has_tag as h LEFT OUTER JOIN tag as t ON t.id = h.tagId)" +
            " ON c.id = h.certificateId WHERE c.id IN (select * from (select id from gift_certificate order by id" +
            " LIMIT ?, ?) as query1)";
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
    private static final String INSERT_VALUES_IN_HAS_TAG_TABLE_SQL
            = "insert into has_tag (certificateId, tagId) values (?, ?)";
    private static final String DELETE_VALUES_IN_HAS_TAG_TABLE_SQL
            = "delete from has_tag where certificateId = ? and tagId = ?";
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

    public JdbcCertificateDaoImpl() {
    }

    /**
     * The setter of the {@link JdbcTemplate}.
     *
     * @param jdbcTemplate is the {@link JdbcTemplate} to set.
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * The setter of the {@link ResultSetExtractor<List<GiftCertificate>>}.
     *
     * @param giftCertificateExtractor is the {@link ResultSetExtractor<List<GiftCertificate>>} to set.
     */
    public void setGiftCertificateExtractor(ResultSetExtractor<List<GiftCertificate>> giftCertificateExtractor) {
        this.giftCertificateExtractor = giftCertificateExtractor;
    }

    /**
     * The setter of the {@link RowMapper<GiftCertificate>}.
     *
     * @param certificateMapper is the {@link RowMapper<GiftCertificate>} to set.
     */
    public void setCertificateMapper(RowMapper<GiftCertificate> certificateMapper) {
        this.certificateMapper = certificateMapper;
    }

    /**
     * Returns all the {@link GiftCertificate}s in the database.
     *
     * @param pageNumber              is the pageNumber query parameter.
     * @param amountEntitiesOnThePage is the amountEntitiesOnThePage query parameter.
     * @return {@link List<GiftCertificate>}.
     */
    @Override
    public List<GiftCertificate> findAllPagination(int pageNumber, int amountEntitiesOnThePage,
                                                   Map<String, String> parameters) {
        String CREATED_FIND_ALL_ENTITIES = ColumnNames.createQuery(parameters);
        List<GiftCertificate> giftCertificates =
                jdbcTemplate.query(CREATED_FIND_ALL_ENTITIES, giftCertificateExtractor,
                        pageNumber * amountEntitiesOnThePage, amountEntitiesOnThePage);
        return giftCertificates;
    }

    private String createQuery(Map<String, String> parameters) {
        String part1 = "select c.id as certificateId, c.name as certificateName, c.description as certificateDescription," +
                " c.duration as certificateDuration, c.create_date as certificateCreateDate," +
                " c.price as certificatePrice, c.last_update_date as certificateLastUpdateDate, t.id as tagId," +
                " t.name as tagName" +
                " from gift_certificate as c" +
                " LEFT OUTER JOIN (has_tag as h LEFT OUTER JOIN tag as t ON t.id = h.tagId) ON c.id = h.certificateId" +
                " WHERE c.id IN (select * from (select id from (select cq.id, tq.name, COUNT(tq.name) as amount" +
                " from (gift_certificate as cq" +
                " LEFT OUTER JOIN (has_tag as hq LEFT OUTER JOIN tag as tq ON tq.id = hq.tagId) ON cq.id = hq.certificateId)";
        String part2_1 = " where cq.name like ";
        String part2_2 = "'%%'";
        String part3_1 = " and cq.description like ";
        String part3_2 = "'%%'";
        String part4_1 = " and tq.name in (%s)";
        String part4_2 = "";
        String part4_3 = "group by cq.id having amount = ";
        String part4_4 = "";
        String part5 = " order by cq.id) as query2";
        String part6 = " LIMIT ?, ?) as query1);";

        Set<Map.Entry<String, String>> set = parameters.entrySet();
        for (Map.Entry<String, String> entry : set) {
            if (entry.getKey().equals("part_cert_name")) {
                part2_2 = "'%" + parameters.get("part_cert_name") + "%'";
            }
            if (entry.getKey().equals("part_descr_name")) {
                part3_2 = "'%" + parameters.get("part_descr_name") + "%'";
            }
            if (entry.getKey().equals("tag_name")) {
                List<String> values = Arrays.asList(parameters.get("tag_name").split(","));
                part4_4 = String.valueOf(values.size());
                for (String value : values) {
                    if (part4_2.equals("")) {
                        part4_2 = part4_2.concat("'").concat(value).concat("'");
                    } else {
                        part4_2 = part4_2.concat(", ").concat("'").concat(value).concat("'");
                    }
                }
            }
        }
        String findAllQuery = part1.concat(part2_1);
        if (part4_2.equals("")) {
            findAllQuery = findAllQuery + part2_2 + part3_1 + part3_2 + part5 + part6;
        } else {
            String part4 = String.format(part4_1, part4_2);
            findAllQuery = findAllQuery + part2_2 + part3_1 + part3_2
                    + String.format(part4_1, part4_2) + part4_3 + part4_4 + part5 + part6;
        }
        return findAllQuery;
    }

    /**
     * Returns all the {@link GiftCertificate}s in the database.
     *
     * @return {@link List<GiftCertificate>}.
     */
    @Override
    public List<GiftCertificate> findAll() {
        return jdbcTemplate.query(FIND_ALL_ENTITIES_SQL_PAGINATION, giftCertificateExtractor);
    }

    /**
     * Returns {@link Optional<GiftCertificate>} by its ID.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<GiftCertificate>}.
     */
    @Override
    public Optional<GiftCertificate> findById(long id) {
        return jdbcTemplate.query(FIND_ENTITY_BY_ID_SQL, giftCertificateExtractor, id).stream().findFirst();
    }

    /**
     * Saves {@link GiftCertificate} in the database.
     *
     * @param giftCertificate is the {@link GiftCertificate} to save.
     */
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

    /**
     * Updates {@link GiftCertificate}.
     *
     * @param giftCertificate is the {@link GiftCertificate} to update.
     */
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

    /**
     * Deletes {@link GiftCertificate} from database by its ID.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_ENTITY_BY_ID_SQL, id);
    }

    /**
     * Finds a {@link Optional<GiftCertificate>} by ist name.
     *
     * @param name the value of the parameter 'name' to find.
     * @return {@link Optional<GiftCertificate>}.
     */
    @Override
    public Optional<GiftCertificate> findByName(String name) {
        return jdbcTemplate.query(FIND_ENTITY_BY_NAME_SQL, giftCertificateExtractor, name).stream().findFirst();
    }

    /**
     * Saves certificateId and tagId in the database.
     *
     * @param certificateId is the id of the {@link GiftCertificate} to save.
     * @param tagId         is the id of the {@link com.epam.esm.model.impl.CertificateTag} to save
     */
    @Override
    public void saveIdsInHas_tagTable(long certificateId, long tagId) {
        jdbcTemplate.update(INSERT_VALUES_IN_HAS_TAG_TABLE_SQL, certificateId, tagId);
    }

    /**
     * Removes the tuple certificateId and tagId from the 'has_tag' table of the database.
     *
     * @param certificateId is the id of the {@link GiftCertificate} to remove.
     * @param tagId         is the id of the {@link CertificateTag} to remove.     *
     */
    @Override
    public void deleteIdsInHas_TagTable(long certificateId, Long tagId) {
        jdbcTemplate.update(DELETE_VALUES_IN_HAS_TAG_TABLE_SQL, certificateId, tagId);
    }

    /**
     * Finds certificate without {@link CertificateTag} by its name.
     *
     * @param name the name to find by.
     * @return {@link Optional<GiftCertificate>}.
     */
    @Override
    public Optional<GiftCertificate> findCertificateWithoutTagsByName(String name) {
        return jdbcTemplate.query(FIND_CERTIFICATE_WITHOUT_TAGS_BY_NAME, certificateMapper, name).stream().findFirst();
    }
}
