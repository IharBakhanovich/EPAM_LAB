package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDAO;
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

    @Override
    public List<CertificateTag> findAll() {
        return jdbcTemplate.query(FIND_ALL_ENTITIES_SQL, certificateTagRowMapper);
    }

    public Optional<CertificateTag> findById(long id) {
//        return jdbcTemplate.queryForObject(FIND_ENTITY_BY_ID_SQL, new Object[]{id}, CertificateTag.class);
        return jdbcTemplate.query(FIND_ENTITY_BY_ID_SQL, certificateTagRowMapper, id).stream().findFirst();
    }

    @Override
    public void save(CertificateTag certificateTag) {
        jdbcTemplate.update(INSERT_ENTITY_SQL,
                certificateTag.getName());
    }

    @Override
    public void update(CertificateTag certificateTag) {
        jdbcTemplate.update(UPDATE_ENTITY_SQL,
                certificateTag.getName(),
                certificateTag.getId());
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_ENTITY_BY_ID_SQL, id);
    }

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

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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