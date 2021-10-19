package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDAO;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.model.impl.CertificateTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The class that implements the {@link TagDAO} interface.
 */
@Component
public class TagDAOImpl implements TagDAO {
    private static final Logger LOGGER = LogManager.getLogger(CertificateDAOImpl.class);

    private static final String FIND_ALL_ENTITIES_SQL = "select tag.id as tagId, tag.name as tagName from tag";
    private static final String INSERT_ENTITY_SQL = "insert into tag (name) values (?)";
    private static final String DELETE_ENTITY_BY_ID_SQL = "delete from tag where id = ?";
    private static final String UPDATE_ENTITY_SQL = "update tag set name = ? where id = ?";
    private static final String FIND_ENTITY_BY_ID_SQL
            = "select tag.id as tagId, tag.name as tagName from tag where id = ?";
    private static final String FIND_ENTITY_BY_NAME_SQL
            = "select tag.id as tagId, tag.name as tagName from tag where name = ?";

    private static final String FIND_ALL_TAGS_BY_CERTIFICATE_ID_SQL
            = "select tag.id as tagId, tag.name as tagName from tag, has_tag" +
            " where tag.id = has_tag.tagId and has_tag.certificateId = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("certificateTagMapper")
    private RowMapper<CertificateTag> certificateTagRowMapper;

    private TagDAOImpl() {
    }

    /**
     * Finds all {@link CertificateTag} entity in the database.
     *
     * @return List of the {@link CertificateTag} objects.
     */
    @Override
    public List<CertificateTag> findAll() {
        return jdbcTemplate.query(FIND_ALL_ENTITIES_SQL, certificateTagRowMapper);
    }

    /**
     * Finds {@link Optional<CertificateTag>} in the database by the id of the {@link CertificateTag}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<CertificateTag>}.
     */
    public Optional<CertificateTag> findById(long id) {
        return jdbcTemplate.query(FIND_ENTITY_BY_ID_SQL, certificateTagRowMapper, id).stream().findFirst();
    }

    /**
     * Saves {@link CertificateTag} in the database.
     *
     * @param certificateTag is the {@link CertificateTag} to save.
     * @throws DuplicateException if a SQLException with the state 23505 or the state 23000 is thrown.
     */
    @Override
    public void save(CertificateTag certificateTag) {
        jdbcTemplate.update(INSERT_ENTITY_SQL,
                certificateTag.getName());
    }

    /**
     * Updates the {@link CertificateTag}.
     *
     * @param certificateTag is the value of the {@link CertificateTag} to update.
     */
    @Override
    public void update(CertificateTag certificateTag) {
        jdbcTemplate.update(UPDATE_ENTITY_SQL,
                certificateTag.getName(),
                certificateTag.getId());
    }

    /**
     * Removes the {@link CertificateTag} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_ENTITY_BY_ID_SQL, id);
    }

    /**
     * Finds all {@link CertificateTag}s by {@link com.epam.esm.model.impl.GiftCertificate} ID.
     *
     * @param id is the ID to find by.
     * @return {@link List<CertificateTag>}.
     */
    @Override
    public List<CertificateTag> findAllTagsByCertificateId(long id) {
        return jdbcTemplate.query(FIND_ALL_TAGS_BY_CERTIFICATE_ID_SQL,
                rs -> {
                    List<CertificateTag> allTagsByCertificateId = new ArrayList<CertificateTag>();
                    while (rs.next()) {
                        CertificateTag certificateTag = new CertificateTag(
                                rs.getLong("tagId"), rs.getString("tagName")
                        );
                        allTagsByCertificateId.add(certificateTag);
                    }
                    return allTagsByCertificateId;
                },
                id);
    }

    /**
     * Sets {@link JdbcTemplate}.
     *
     * @param jdbcTemplate is the {@link JdbcTemplate} to set.
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Sets the {@link RowMapper<CertificateTag>}.
     *
     * @param certificateTagRowMapper is hte {@link RowMapper<CertificateTag>} to set.
     */
    public void setCertificateTagRowMapper(RowMapper<CertificateTag> certificateTagRowMapper) {
        this.certificateTagRowMapper = certificateTagRowMapper;
    }

    /**
     * Finds {@link Optional<CertificateTag>} in the database by the id of the {@link CertificateTag}.
     *
     * @param name is the {@link String} to find.
     * @return {@link Optional<CertificateTag>}.
     */
    @Override
    public Optional<CertificateTag> findByName(String name) {
        return jdbcTemplate.query(FIND_ENTITY_BY_NAME_SQL, certificateTagRowMapper, name).stream().findFirst();
    }
}