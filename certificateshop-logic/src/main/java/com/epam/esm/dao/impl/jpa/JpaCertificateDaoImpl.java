package com.epam.esm.dao.impl.jpa;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * The class that implements the {@link TagDao} interface.
 */
@Profile("dev_jpa")
@Repository
@Transactional
public class JpaCertificateDaoImpl implements CertificateDao {

    private GiftCertificateRepository giftCertificateRepository;
    private EntityManager entityManager;

    @Autowired
    public JpaCertificateDaoImpl(GiftCertificateRepository giftCertificateRepository,
                                 EntityManager entityManager) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.entityManager = entityManager;
    }

    /**
     * Saves {@link GiftCertificate} in the database.
     *
     * @param entity is the {@link GiftCertificate} to save.
     */
    @Override
    public void save(GiftCertificate entity) {
        giftCertificateRepository.save(entity);
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
        return giftCertificateRepository
                .findAll(PageRequest.of(pageNumber,
                        amountEntitiesOnThePage, Sort.by(Sort.Direction.ASC, "id")))
                .getContent();
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
        return giftCertificateRepository.findById(id);
    }

    /**
     * Updates the {@link GiftCertificate}.
     *
     * @param entity is the value of the {@link GiftCertificate} to update.
     */
    @Override
    public void update(GiftCertificate entity) {
        giftCertificateRepository.save(entity);
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
        return giftCertificateRepository.findByName(name);
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
