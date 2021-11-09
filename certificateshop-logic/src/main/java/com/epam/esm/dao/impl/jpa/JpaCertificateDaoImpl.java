package com.epam.esm.dao.impl.jpa;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.ListToSetConverter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The class that implements the {@link TagDao} interface.
 */
@Profile("dev_jpa")
@Repository
@Transactional
public class JpaCertificateDaoImpl implements CertificateDao {
    private static final String FIND_ALL_ENTITIES_SQL_PAGINATION
            = "select c.id as certificateId, c.name as certificateName," +
            " c.description as certificateDescription, c.duration as certificateDuration," +
            " c.create_date as certificateCreateDate, c.price as certificatePrice," +
            " c.last_update_date as certificateLastUpdateDate, t.id as tagId, t.name as tagName" +
            " from gift_certificate as c LEFT OUTER JOIN (has_tag as h LEFT OUTER JOIN tag as t ON t.id = h.tagId)" +
            " ON c.id = h.certificateId WHERE c.id IN (select * from (select id from gift_certificate order by id" +
            " LIMIT ?, ?) as query1)";
    private static final List<String> CERTIFICATE_HEADERS = Arrays.asList(ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_ID,
            ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_NAME, ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_DESCRIPTION,
            ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_DURATION, ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_CREATE_DATE,
            ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_PRICE, ColumnNames.TABLE_GIFT_CERTIFICATE_COLUMN_LAST_UPDATE_DATE,
            ColumnNames.TABLE_TAG_COLUMN_ID, ColumnNames.TABLE_TAG_COLUMN_NAME);

    private GiftCertificateRepository giftCertificateRepository;
    private EntityManager entityManager;
    private ListToSetConverter listToSetConverter;
    private GiftCertificateExtractor giftCertificateExtractor;

    @Autowired
    public JpaCertificateDaoImpl(GiftCertificateRepository giftCertificateRepository, EntityManager entityManager,
                                 ListToSetConverter listToSetConverter, GiftCertificateExtractor giftCertificateExtractor) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.entityManager = entityManager;
        this.listToSetConverter = listToSetConverter;
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

//        giftCertificateRepository.save(entity);
    }

    /**
     * Finds all {@link GiftCertificate} entity in the database.
     *
     * @param pageNumber              is the pageNumber query parameter.
     * @param amountEntitiesOnThePage is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link GiftCertificate} objects.
     */
    @Override
    public List<GiftCertificate> findAllPagination(int pageNumber, int amountEntitiesOnThePage) {
        Query query = entityManager.createNativeQuery(FIND_ALL_ENTITIES_SQL_PAGINATION)
                .setParameter(1, pageNumber * amountEntitiesOnThePage)
                .setParameter(2, amountEntitiesOnThePage);
        List<Object[]> resultList = query.getResultList();
        List<List<Object>> result = convertListOfArrayToListOfLists(resultList);
        return getEntities(result);
//        return giftCertificateRepository
//                .findAll(PageRequest.of(pageNumber,
//                        amountEntitiesOnThePage, Sort.by(Sort.Direction.ASC, "id")))
//                .getContent();

    }

    private List<GiftCertificate> getEntities(List<List<Object>> result) {
        ResultSet resultSet;
        List<GiftCertificate> certificates = new ArrayList<>();
        try {
            resultSet = listToSetConverter.getResultSet(CERTIFICATE_HEADERS, result);
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
        return giftCertificateRepository.findAll();
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
//        return giftCertificateRepository.findById(id);
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
        //giftCertificateRepository.save(entity);
    }

    /**
     * Deletes the {@link GiftCertificate} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {
        giftCertificateRepository.deleteById(id);
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
//        return giftCertificateRepository.findByName(name);
    }

    /**
     * Saves certificateId and tagId in the database.
     *
     * @param certificateId is the id of the {@link GiftCertificate} to save.
     * @param tagId         is the id of the {@link CertificateTag} to save
     */
    @Override
    public void saveIdsInHas_tagTable(long certificateId, long tagId) {

    }

    /**
     * Removes the tuple certificateId and tagId from the 'has_tag' table of the database.
     *
     * @param certificateId is the id of the {@link GiftCertificate} to remove.
     * @param tagId         is the id of the {@link CertificateTag} to remove.     *
     */
    @Override
    public void deleteIdsInHas_TagTable(long certificateId, Long tagId) {

    }

    /**
     * Finds certificate without {@link CertificateTag} by its name.
     *
     * @param name the name to find by.
     * @return {@link Optional<GiftCertificate>}.
     */
    @Override
    public Optional<GiftCertificate> findCertificateWithoutTagsByName(String name) {
        return Optional.empty();
    }
}
