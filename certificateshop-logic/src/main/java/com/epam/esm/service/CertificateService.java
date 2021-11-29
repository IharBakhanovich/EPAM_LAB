package com.epam.esm.service;

import com.epam.esm.model.impl.GiftCertificate;

import java.util.List;
import java.util.Map;

/**
 * The interface that defines the certificate api of the application.
 */
public interface CertificateService {

    /**
     * Creates a new {@link GiftCertificate} in the system.
     *
     * @param giftCertificate is the {@link GiftCertificate} to create.
     * @return created {@link GiftCertificate}
     */
    GiftCertificate createCertificate(GiftCertificate giftCertificate);

    /**
     * Returns all certificates in the system.
     *
     * @param filters the filters and parameters to apply to the values to be returned.
     * @return {@link List<GiftCertificate}.
     */
    List<GiftCertificate> findAllCertificates(Map<String, String> filters);

    /**
     * Returns a certificate by its id.
     *
     * @param id is the id of the {@link GiftCertificate} to find in the system.
     */
    GiftCertificate findCertificateById(long id);

    /**
     * Updates a certificate.
     *
     * @param giftCertificate is the {@link GiftCertificate} to update.
     */
    GiftCertificate updateCertificate(long id, GiftCertificate giftCertificate);

    /**
     * Deletes a certificate by its id.
     *
     * @param id is the id of the {@link GiftCertificate} to delete.
     */
    void deleteCertificate(long id);

    /**
     * Returns a certificate by its name.
     *
     * @param name is the name of the {@link GiftCertificate} to find in the system.
     */
    GiftCertificate findCertificateByName(String name);
}
