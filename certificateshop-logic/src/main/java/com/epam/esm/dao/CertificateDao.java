package com.epam.esm.dao;

import com.epam.esm.exception.DuplicateException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CertificateDao extends Dao<GiftCertificate> {
    /**
     * Saves {@link GiftCertificate} in the database.
     *
     * @param entity is the {@link GiftCertificate} to save.
     * @throws DuplicateException if a SQLException with the state 23505 or the state 23000 is thrown.
     */
    @Override
    void save(GiftCertificate entity);

    /**
     * Finds all {@link GiftCertificate} entity in the database.
     *
     * @param pageNumber is the pageNumber query parameter.
     * @param amountEntitiesOnThePage  is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link GiftCertificate} objects.
     */
    List<GiftCertificate> findAllPagination(int pageNumber, int amountEntitiesOnThePage, Map<String, String> parameters);

    /**
     * Returns all the {@link GiftCertificate}s in the database.
     *
     * @return {@link List<GiftCertificate>}.
     */
    @Override
    List<GiftCertificate> findAll();

    /**
     * Finds {@link Optional<GiftCertificate>} in the database by the id of the {@link GiftCertificate}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<GiftCertificate>}.
     */
    @Override
    Optional<GiftCertificate> findById(long id);

    /**
     * Updates the {@link GiftCertificate}.
     *
     * @param entity is the value of the {@link GiftCertificate} to update.
     */
    @Override
    void update(GiftCertificate entity);

    /**
     * Deletes the {@link GiftCertificate} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    void delete(long id);

    /**
     * Finds {@link Optional<GiftCertificate>} in the database by the name of the {@link GiftCertificate}.
     *
     * @param name is the {@link String} to find.
     * @return {@link Optional<GiftCertificate>}.
     */
    Optional<GiftCertificate> findByName(String name);

    /**
     * Saves certificateId and tagId in the database.
     *
     * @param certificateId is the id of the {@link GiftCertificate} to save.
     * @param tagId         is the id of the {@link com.epam.esm.model.impl.CertificateTag} to save
     */
    void saveIdsInHas_tagTable(long certificateId, long tagId);

    /**
     * Removes the tuple certificateId and tagId from the 'has_tag' table of the database.
     *
     * @param certificateId is the id of the {@link GiftCertificate} to remove.
     * @param tagId         is the id of the {@link CertificateTag} to remove.     *
     */
    void deleteIdsInHas_TagTable(long certificateId, Long tagId);

    /**
     * Finds certificate without {@link CertificateTag} by its name.
     *
     * @param name the name to find by.
     * @return {@link Optional<GiftCertificate>}.
     */
    Optional<GiftCertificate> findCertificateWithoutTagsByName(String name);
}
