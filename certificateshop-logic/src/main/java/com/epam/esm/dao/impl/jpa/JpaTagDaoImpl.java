package com.epam.esm.dao.impl.jpa;

import com.epam.esm.dao.TagDao;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.User;
import com.epam.esm.repository.CertificateTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

/**
 * The class that implements the {@link TagDao} interface.
 */
@Profile("dev_jpa")
@Repository
public class JpaTagDaoImpl implements TagDao {
    public static final String FIND_MOST_POPULAR_TAG_BY_THE_BEST_USER =
            "SELECT tag.id as tagId, tag.name as tagName FROM tag" +
                    " where id = (SELECT query3.tagIdAfterCount" +
                    " FROM (SELECT ht.tagId as tagIdAfterCount, count(ht.tagId) as amountOfTag" +
                    " FROM userorder as uo LEFT OUTER JOIN" +
                    " (has_tag as ht LEFT OUTER JOIN userorder_certificate as uoc on ht.certificateId = uoc.certificateId)" +
                    " ON uo.id = uoc.userOrderId" +
                    " WHERE uo.userId = (SELECT query1.id" +
                    " FROM (SELECT uo.userId as id, SUM(uoc.certificatePrice) as costAllCertificates" +
                    " FROM userorder_certificate as uoc LEFT OUTER JOIN userorder as uo ON uoc.userOrderId = uo.id" +
                    " GROUP BY userId ORDER BY costAllCertificates DESC) as query1 LIMIT 1)" +
                    " GROUP BY ht.tagId ORDER BY amountOfTag DESC) as query3 LIMIT 1)";
    private static final String DELETE_VALUES_IN_HAS_TAG_TABLE_SQL
            = "delete from has_tag where tagId = ?";
    private CertificateTagRepository certificateTagRepository;
    private EntityManager entityManager;

    @Autowired
    public JpaTagDaoImpl(CertificateTagRepository certificateTagRepository,
                         EntityManager entityManager) {
        this.certificateTagRepository = certificateTagRepository;
        this.entityManager = entityManager;
    }

    /**
     * Saves {@link CertificateTag} in the database.
     *
     * @param entity is the {@link CertificateTag} to save.
     * @throws DuplicateException if a SQLException with the state 23505 or the state 23000 is thrown.
     */
    @Override
    public void save(CertificateTag entity) throws DuplicateException {
        entityManager.persist(entity);
    }

    /**
     * Finds all {@link CertificateTag} entity in the database.
     *
     * @param pageNumber              is the value of the records, which should be passed before fetching the data.
     * @param amountEntitiesOnThePage is the value of the records, which should be fetched from the database.
     * @return List of the {@link CertificateTag} objects.
     */
    @Override
    public List<CertificateTag> findAllPagination(int pageNumber, int amountEntitiesOnThePage) {
        return entityManager.createQuery("select t from Tag t order by t.id", CertificateTag.class)
                .setFirstResult(amountEntitiesOnThePage * pageNumber)
                .setMaxResults(amountEntitiesOnThePage)
                .getResultList();
    }

    /**
     * Finds all {@link CertificateTag} entity in the database.
     *
     * @return List of the {@link CertificateTag} objects.
     */
    @Override
    public List<CertificateTag> findAll() {
        return entityManager.createQuery("select t from Tag t order by t.id", CertificateTag.class)
                .getResultList();
    }

    /**
     * Finds {@link Optional <CertificateTag>} in the database by the id of the {@link CertificateTag}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<CertificateTag>}.
     */
    @Override
    public Optional<CertificateTag> findById(long id) {
        return entityManager
                .createQuery("select t from Tag t where t.id = :id", CertificateTag.class)
                .setParameter("id", id).getResultList().stream().findFirst();
    }

    /**
     * Updates the {@link CertificateTag}.
     *
     * @param entity is the value of the {@link CertificateTag} to update.
     */
    @Override
    public void update(CertificateTag entity) {
        CertificateTag tag = entityManager.find(CertificateTag.class, entity.getId());
        entityManager.createQuery("UPDATE Tag t set t.name = :name where t.id = :id")
                .setParameter("name", entity.getName()).setParameter("id", entity.getId()).executeUpdate();
        entityManager.refresh(tag);
    }

    /**
     * Removes the {@link CertificateTag} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {
        entityManager
                .createQuery("delete from Tag t where t.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    /**
     * Finds all {@link CertificateTag}s by {@link GiftCertificate} ID.
     *
     * @param id is the ID to find by.
     * @return {@link List<CertificateTag>}.
     */
    @Override
    public List<CertificateTag> findAllTagsByCertificateId(long id) {
        return certificateTagRepository.findCertificateTagsByCertificateId(id);
    }

    /**
     * Finds {@link Optional<CertificateTag>} in the database by the id of the {@link CertificateTag}.
     *
     * @param name is the {@link String} to find.
     * @return {@link Optional<CertificateTag>}.
     */
    @Override
    public Optional<CertificateTag> findByName(String name) {
        return entityManager
                .createQuery("select t from Tag t where t.name = :name", CertificateTag.class)
                .setParameter("name", name).getResultList().stream().findFirst();
    }

    /**
     * Removes records from 'has_tag' table by tagId.
     *
     * @param tagId is the id to remove by.
     */
    @Override
    public void deleteFromHasTagByTagId(long tagId) {
        List resultList = entityManager.createNativeQuery(
                        "select certificateId as certId, tagId as tId from has_tag where tagId = ?")
                .setParameter(1, tagId).getResultList();
        if (!resultList.isEmpty()) {
            entityManager.createNativeQuery(DELETE_VALUES_IN_HAS_TAG_TABLE_SQL)
                    .setParameter(1, tagId).executeUpdate();
        }
    }

    /**
     * Finds the most popular {@link CertificateTag} of the {@link User}
     * with the biggest sum of order price.
     *
     * @return {@link Optional<CertificateTag>}.
     */
    @Override
    public Optional<CertificateTag> findTheMostPopularTagOfTheBestUser() {
        Query query = entityManager.createNativeQuery(FIND_MOST_POPULAR_TAG_BY_THE_BEST_USER);
        Object[] result = (Object[]) query.getSingleResult();
        return Optional.of(new CertificateTag(Long.parseLong(result[0].toString()), result[1].toString()));
    }
}
