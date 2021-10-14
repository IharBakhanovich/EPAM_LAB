package com.epam.esm.service;

import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.CertificateTag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

//    /**
//     * Deletes all tags from the {@link GiftCertificate}.
//     *
//     * @param id is the id of the {@link GiftCertificate} which {@link CertificateTag}s to delete.
//     */
//    void deleteAllTagsCertificate(long id);

//    /**
//     * Deletes {@link CertificateTag} from the {@link GiftCertificate}.
//     *
//     * @param certificateId is the id of the {@link GiftCertificate} which {@link CertificateTag} to delete.
//     * @param tagId is the id of the {@link CertificateTag}, which is to delete.
//     */
//    void deleteTagOfCertificate(long certificateId, long tagId);
//
//    /**
//     * Adds the existed in the system {@link CertificateTag} to the {@link GiftCertificate}.
//     *
//     * @param certificateId is the id of the {@link GiftCertificate} to which {@link CertificateTag} is to add.
//     * @param tagId is the id of the {@link CertificateTag}, which is to add.
//     */
//    GiftCertificate addTagToCertificate(long certificateId, long tagId);
//
//    /**
//     * Creates a new {@link CertificateTag} and adds it to the {@link GiftCertificate}.
//     *
//     * @param certificateId is the id of the {@link GiftCertificate} to which {@link CertificateTag} is to add.
//     * @param certificateTag is the {@link CertificateTag}, which is to add.
//     */
//    GiftCertificate addNewTagToCertificate(long certificateId, CertificateTag certificateTag);
//
//    /**
//     * Updates the {@link GiftCertificate}.
//     *
//     * @param certificateName is the name of the {@link GiftCertificate}, which is to update.
//     * @param giftCertificate is the {@link GiftCertificate}, which contains the updated data.
//     */
//    GiftCertificate updateCertificateByName(String certificateName, GiftCertificate giftCertificate);

//    /**
//     * Returns all {@link GiftCertificate}s which has the {@link CertificateTag} with the name equals {@param tagName}.
//     *
//     * @param tagName is the name of the {@link CertificateTag}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    List<GiftCertificate> findCertificateByTagName(String tagName);
//
//    /**
//     * Returns all {@link GiftCertificate}s which name contains {@param partSertName}.
//     *
//     * @param partSertName is the part of the name of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    List<GiftCertificate> findCertificatesByPartSertName(String partSertName);
//
//    /**
//     * Returns all {@link GiftCertificate}s which name contains {@param partDescrName}.
//     *
//     * @param partDescrName is the part of the description of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    List<GiftCertificate> findCertificatesByPartDescrName(String partDescrName);
//
//    /**
//     * Returns all {@link GiftCertificate}s which has the {@link CertificateTag}
//     * with the name equals {@param tagName} and which name contains {@param partSertName}.
//     *
//     * @param tagName is the name of the {@link CertificateTag}, which is to find.
//     * @param partSertName is the part of the name of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    List<GiftCertificate> findCertificatesByTagNameAndPartSertName(String tagName, String partSertName);
//
//    /**
//     * Returns all {@link GiftCertificate}s which name contains {@param partSertName} and which description
//     * contains {@param partDescrName}.
//     *
//     * @param partDescrName is the part of the description of the {@link GiftCertificate}, which is to find.
//     * @param partSertName is the part of the name of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    List<GiftCertificate> findCertificatesByPartSertNameAndPartDescrName(String partSertName, String partDescrName);
//
//    /**
//     * Returns all {@link GiftCertificate}s which has the {@link CertificateTag}
//     * with the name equals {@param tagName} and which description contains {@param partDescrName}.
//     *
//     * @param tagName is the name of the {@link CertificateTag}, which is to find.
//     * @param partDescrName is the part of the description of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    List<GiftCertificate> findCertificatesByTagNameAndPartDescrName(String tagName, String partDescrName);
//
//    /**
//     * Returns all {@link GiftCertificate}s which has the {@link CertificateTag}
//     * with the name equals {@param tagName}, which description contains {@param partDescrName}
//     * and which name contains {@param partSertName}.
//     *
//     * @param tagName is the name of the {@link CertificateTag}, which is to find.
//     * @param partSertName is the part of the name of the {@link GiftCertificate} which is to find.
//     * @param partDescrName is the part of the description of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    List<GiftCertificate> findCertificatesByAllParameters(String tagName, String partSertName, String partDescrName);
}
