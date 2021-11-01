package com.epam.esm.service;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.User;

import java.util.List;
import java.util.Map;

/**
 * The interface that defines the certificate api of the application.
 */
public interface TagService {

    /**
     * Creates a new {@link CertificateTag} in the system.
     *
     * @param certificateTag is the {@link CertificateTag} to create.
     */
    CertificateTag createCertificateTag(CertificateTag certificateTag);

    /**
     * Returns all {@link CertificateTag}s in the system.
     */
    List<CertificateTag> findAllCertificateTags(Map<String, String> parameters);

    /**
     * Returns a {@link CertificateTag} by its id.
     *
     * @param id is the id to find in the system.
     */
    CertificateTag findCertificateTagById(long id);

    /**
     * Updates a {@link CertificateTag}.
     *
     * @param tagId          is the id of the {@link CertificateTag}, which is to update.
     * @param certificateTag is the {@link CertificateTag} to update.
     */
    CertificateTag updateCertificateTag(long tagId, CertificateTag certificateTag);

    /**
     * Deletes a {@link CertificateTag} by its id.
     *
     * @param id is the id of the {@link CertificateTag} to delete.
     */
    void deleteCertificateTag(long id);

    /**
     * Returns a {@link CertificateTag} by its name.
     *
     * @param name is the name of the {@link CertificateTag} to find in the system.
     */
    CertificateTag findCertificateTagByName(String name);

    /**
     * Returns the most popular {@link CertificateTag} of the {@link User}
     * with the biggest sum of order price.
     *
     * @return {@link CertificateTag}
     */
    CertificateTag mostPopularTagOfTheBestUser();
}
