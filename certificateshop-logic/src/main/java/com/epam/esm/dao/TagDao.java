package com.epam.esm.dao;

import com.epam.esm.exception.DuplicateException;
import com.epam.esm.model.impl.CertificateTag;

import java.util.List;
import java.util.Optional;

public interface TagDao extends Dao<CertificateTag> {
    /**
     * Saves {@link CertificateTag} in the database.
     *
     * @param entity is the {@link CertificateTag} to save.
     * @throws DuplicateException if a SQLException with the state 23505 or the state 23000 is thrown.
     */
    @Override
    void save(CertificateTag entity) throws DuplicateException;

    /**
     * Finds all {@link CertificateTag} entity in the database.
     *
     * @param pageNumber              is the number of the page.
     * @param amountEntitiesOnThePage is the value of the records,
     *                                which should be fetched from the database and showed on the page.
     * @return List of the {@link CertificateTag} objects.
     */
    List<CertificateTag> findAllPagination(int pageNumber, int amountEntitiesOnThePage);

    /**
     * Finds all {@link CertificateTag} entity in the database.
     *
     * @return List of the {@link CertificateTag} objects.
     */
    @Override
    List<CertificateTag> findAll();

    /**
     * Finds {@link Optional<CertificateTag>} in the database by the id of the {@link CertificateTag}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<CertificateTag>}.
     */
    @Override
    Optional<CertificateTag> findById(long id);

    /**
     * Updates the {@link CertificateTag}.
     *
     * @param entity is the value of the {@link CertificateTag} to update.
     */
    @Override
    void update(CertificateTag entity);

    /**
     * Removes the {@link CertificateTag} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    void delete(long id);

    /**
     * Finds all {@link CertificateTag}s by {@link com.epam.esm.model.impl.GiftCertificate} ID.
     *
     * @param id is the ID to find by.
     * @return {@link List<CertificateTag>}.
     */
    List<CertificateTag> findAllTagsByCertificateId(long id);

    /**
     * Finds {@link Optional<CertificateTag>} in the database by the id of the {@link CertificateTag}.
     *
     * @param name is the {@link String} to find.
     * @return {@link Optional<CertificateTag>}.
     */
    Optional<CertificateTag> findByName(String name);

    /**
     * Removes records from 'has_tag' table by tagId.
     *
     * @param tagId is the id to remove by.
     */
    void deleteFromHasTagByTagId(long tagId);

    /**
     * Finds the most popular {@link CertificateTag} of the {@link com.epam.esm.model.impl.User}
     * with the biggest sum of order price.
     *
     * @return {@link Optional<CertificateTag>}.
     */
    Optional<CertificateTag> findTheMostPopularTagOfTheBestUser();
}
