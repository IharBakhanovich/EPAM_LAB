package com.epam.esm.controller;

import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API to work with {@link GiftCertificate}s of the GiftCertificatesShop.
 */
@RestController
@RequestMapping("/certificates")
public class GiftCertificateController {
    private final CertificateService certificateService;

    /**
     * Constructs the {@link GiftCertificateController}.
     *
     * @param certificateService is the service to inject.
     */
    @Autowired
    public GiftCertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    /**
     * The method that realises the 'GET /certificates' query.
     *
     * @param parameters: there are following parameters, which can be applied to the query:
     *                  - tag_name=123 is the name of the tag by which the query will be executed and only certificates,
     *                  that contain the tag with a mentioned name will be shown;
     *                  - part_cert_name=123 is the part of a certificate name. Only certificates, which contain
     *                  the value in their names will be shown;
     *                  - part_descr_name=123 is the part of a description name. Only certificates, which contain
     *                  the value in their descriptions will be shown;
     *                  - sortByName=asc/desc is the parameter to sort all the certificates by name. ASC means
     *                  to sort in normal order, DESC - the sort order is reversed;
     *                  -  sortByDate=asc/desc is the parameter to sort all the certificates by CreateDate.
     *                  ASC means to sort in normal order, DESC - the sort order is reversed.
     * @return {@link List<GiftCertificate>} - {@link GiftCertificate}s in the system.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GiftCertificate> certificates(@RequestParam Map<String, String> parameters) {
        return certificateService.findAllCertificates(parameters);
    }

    /**
     * The method that realises the 'GET /certificates/{certificateId}' query.
     *
     * @param certificateId is the ID of the {@link GiftCertificate} to find.
     * @return {@link GiftCertificate} with the certificateId if such an id exists in the system.
     */
    @GetMapping(value = "/{certificateId}")
    @ResponseStatus(HttpStatus.OK)
    public GiftCertificate certificate(@PathVariable("certificateId") long certificateId) {
        return certificateService.findCertificateById(certificateId);
    }

    /**
     * The method that realises the 'DELETE /certificates/{certificateId}' query.
     *
     * @param certificateId is the ID of the {@link GiftCertificate} to delete.
     */
    @DeleteMapping(value = "/{certificateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificate(@PathVariable("certificateId") long certificateId) {
        certificateService.deleteCertificate(certificateId);
    }

    /**
     * The method that realises the 'POST /certificates' query.
     *
     * @param newGiftCertificate is the {@link GiftCertificate} to create.
     * @return the created {@link GiftCertificate}.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GiftCertificate addNewCertificate(@RequestBody GiftCertificate newGiftCertificate) {
        return certificateService.createCertificate(newGiftCertificate);
    }

    /**
     * The method that realises the 'PUT /certificates/{certificateId}' query and updates
     * the {@link GiftCertificate} with the id equals {@param certificateId}.
     *
     * @param giftCertificate is the {@link GiftCertificate} to update.
     * @param certificateId   is the id of the {@link GiftCertificate}, which is to update.
     * @return the updated {@link GiftCertificate}.
     */
    @PutMapping(value = "/{certificateId}")
    @ResponseStatus(HttpStatus.OK)
    public GiftCertificate updateGiftCertificate(@PathVariable("certificateId") long certificateId,
                                                 @RequestBody GiftCertificate giftCertificate) {
        return certificateService.updateCertificate(certificateId, giftCertificate);
    }
}