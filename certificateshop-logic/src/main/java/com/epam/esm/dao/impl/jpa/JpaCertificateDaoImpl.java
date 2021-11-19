package com.epam.esm.dao.impl.jpa;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.ListToResultSetConverter;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.dao.impl.jdbc.GiftCertificateExtractor;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.util.*;

/**
 * The class that implements the {@link TagDao} interface.
 */
@Profile("dev_jpa")
@Repository
@Transactional
public class JpaCertificateDaoImpl implements CertificateDao {
    private static final List<String> CERTIFICATE_HEADERS = Arrays.asList(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_ID,
            ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_NAME, ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_DESCRIPTION,
            ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_DURATION, ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_CREATE_DATE,
            ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_PRICE, ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_LAST_UPDATE_DATE,
            ColumnNames.TABLE_TAG_COLUMN_ID, ColumnNames.TABLE_TAG_COLUMN_NAME);
    private static final String INSERT_VALUES_IN_HAS_TAG_TABLE_SQL
            = "insert into has_tag (certificateId, tagId) values (?, ?)";
    private static final String DELETE_VALUES_IN_HAS_TAG_TABLE_SQL
            = "delete from has_tag where certificateId = ? and tagId = ?";
    public static final String SELECT_CERTIFICATE_ID_AS_CERT_ID_TAG_ID_AS_T_ID_FROM_HAS_TAG_WHERE_CERTIFICATE_ID_AND_TAG_ID
            = "select certificateId as certId, tagId as tId from has_tag where certificateId = ? and tagId = ?";

    private EntityManager entityManager;
    private ListToResultSetConverter listToResultSetConverter;
    private GiftCertificateExtractor giftCertificateExtractor;

    @Autowired
    public JpaCertificateDaoImpl(EntityManager entityManager, ListToResultSetConverter listToResultSetConverter,
                                 GiftCertificateExtractor giftCertificateExtractor) {
        this.entityManager = entityManager;
        this.listToResultSetConverter = listToResultSetConverter;
        this.giftCertificateExtractor = giftCertificateExtractor;
    }

    /**
     * Saves {@link GiftCertificate} in the database.
     *
     * @param entity is the {@link GiftCertificate} to save.
     */
    @Override
    public void save(GiftCertificate entity) {
        entityManager.persist(entity);
    }

    /**
     * Finds all {@link GiftCertificate} entity in the database.
     *
     * @param pageNumber              is the pageNumber query parameter.
     * @param amountEntitiesOnThePage is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link GiftCertificate} objects.
     */
    @Override
    public List<GiftCertificate> findAllPagination(int pageNumber, int amountEntitiesOnThePage, Map<String, String> parameters) {
        String CREATED_FIND_ALL_ENTITIES = ColumnNames.createQuery(parameters);
        Query query = entityManager.createNativeQuery(CREATED_FIND_ALL_ENTITIES)
                .setParameter(1, pageNumber * amountEntitiesOnThePage)
                .setParameter(2, amountEntitiesOnThePage);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        return getEntities(result);
    }

    private List<GiftCertificate> getEntities(List<List<Object>> result) {
        ResultSet resultSet;
        List<GiftCertificate> certificates = new ArrayList<>();
        try {
            resultSet = listToResultSetConverter.convertToResultSet(CERTIFICATE_HEADERS, result);
            certificates = giftCertificateExtractor.extractData(resultSet);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return certificates;
    }

    private List<List<Object>> convertListOfArrayToListOfLists(List<Object[]> resultList) {
        List<List<Object>> result = new ArrayList();
        for (Object[] objects : resultList) {
            List<Object> listOfObjects = new ArrayList<>(Arrays.asList(objects));
            result.add(listOfObjects);
        }
        return result;
    }

    /**
     * Returns all the {@link GiftCertificate}s in the database.
     *
     * @return {@link List<GiftCertificate>}.
     */
    @Override
    public List<GiftCertificate> findAll() {
        return entityManager.createQuery("select c from Certificate c order by c.id", GiftCertificate.class)
                .getResultList();
    }

    /**
     * Finds {@link Optional <GiftCertificate>} in the database by the id of the {@link GiftCertificate}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<GiftCertificate>}.
     */
    @Override
    public Optional<GiftCertificate> findById(long id) {
        return entityManager
                .createQuery("select c from Certificate c where c.id = :id", GiftCertificate.class)
                .setParameter("id", id)
                .getResultList().stream().findFirst();
    }

    /**
     * Updates the {@link GiftCertificate}.
     *
     * @param entity is the value of the {@link GiftCertificate} to update.
     */
    @Override
    public void update(GiftCertificate entity) {
        GiftCertificate giftCertificate = entityManager.find(GiftCertificate.class, entity.getId());
        giftCertificate.setName(entity.getName());
        giftCertificate.setDescription(entity.getDescription());
        giftCertificate.setPrice(entity.getPrice());
        giftCertificate.setDuration(entity.getDuration());
        giftCertificate.setTags(entity.getTags());
    }

    /**
     * Deletes the {@link GiftCertificate} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {
        entityManager
                .createQuery("delete from Certificate c where c.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    /**
     * Finds {@link Optional<GiftCertificate>} in the database by the name of the {@link GiftCertificate}.
     *
     * @param name is the {@link String} to find.
     * @return {@link Optional<GiftCertificate>}.
     */
    @Override
    public Optional<GiftCertificate> findByName(String name) {
        return entityManager
                .createQuery("select c from Certificate c where c.name = :name", GiftCertificate.class)
                .setParameter("name", name)
                .getResultList().stream().findFirst();
    }

    /**
     * Saves certificateId and tagId in the database.
     *
     * @param certificateId is the id of the {@link GiftCertificate} to save.
     * @param tagId         is the id of the {@link CertificateTag} to save
     */
    @Override
    public void saveIdsInHasTagTable(long certificateId, long tagId) {
        List resultList = entityManager.createNativeQuery(
                        SELECT_CERTIFICATE_ID_AS_CERT_ID_TAG_ID_AS_T_ID_FROM_HAS_TAG_WHERE_CERTIFICATE_ID_AND_TAG_ID)
                .setParameter(1, certificateId).setParameter(2, tagId).getResultList();
        if(resultList.isEmpty()) {
            entityManager.createNativeQuery(INSERT_VALUES_IN_HAS_TAG_TABLE_SQL).executeUpdate();
        }
    }

    /**
     * Removes the tuple certificateId and tagId from the 'has_tag' table of the database.
     *
     * @param certificateId is the id of the {@link GiftCertificate} to remove.
     * @param tagId         is the id of the {@link CertificateTag} to remove.     *
     */
    @Override
    public void deleteIdsFromHasTagTable(long certificateId, Long tagId) {
        List resultList = entityManager.createNativeQuery(
                        SELECT_CERTIFICATE_ID_AS_CERT_ID_TAG_ID_AS_T_ID_FROM_HAS_TAG_WHERE_CERTIFICATE_ID_AND_TAG_ID)
                .setParameter(1, certificateId).setParameter(2, tagId).getResultList();
        if(!resultList.isEmpty()) {
            entityManager.createNativeQuery(DELETE_VALUES_IN_HAS_TAG_TABLE_SQL)
                    .setParameter(1, certificateId).setParameter(2, tagId).executeUpdate();
        }
    }
}
