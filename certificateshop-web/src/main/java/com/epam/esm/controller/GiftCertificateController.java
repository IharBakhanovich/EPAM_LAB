package com.epam.esm.controller;

import com.epam.esm.dao.impl.ColumnNames;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
     *                    - tag_name=123 is the name of the tag by which the query will be executed and only certificates,
     *                    that contain the tag with a mentioned name will be shown;
     *                    - part_cert_name=123 is the part of a certificate name. Only certificates, which contain
     *                    the value in their names will be shown;
     *                    - part_descr_name=123 is the part of a description name. Only certificates, which contain
     *                    the value in their descriptions will be shown;
     *                    - sortByName=asc/desc is the parameter to sort all the certificates by name. ASC means
     *                    to sort in normal order, DESC - the sort order is reversed;
     *                    -  sortByDate=asc/desc is the parameter to sort all the certificates by CreateDate.
     *                    ASC means to sort in normal order, DESC - the sort order is reversed.
     *                    -  offset=0/MAX_VALUE is the long to pass records from database.
     *                    To fetch records from 6 record 'offset' should be set to 5.
     *                    - limit = 0/MAX_VALUE is the long to set how many records should be fetched.
     * @return {@link List<GiftCertificate>} - {@link GiftCertificate}s in the system.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<EntityModel<GiftCertificate>> certificates(@RequestParam Map<String, String> parameters) {
        parameters = ColumnNames.validateParameters(parameters, ColumnNames.DEFAULT_ENTITIES_ON_THE_PAGE);
        List<GiftCertificate> certificates
                = certificateService.findAllCertificates(parameters);
        long offset = Long.parseLong(parameters.get("offset"));
        long limit = Long.parseLong(parameters.get("limit"));
        Map<String, String> paramsNext = ColumnNames.createNextParameters(certificates, offset, limit);
        Map<String,String> paramsPrev = ColumnNames.createPrevParameters(certificates, offset, limit);

        List<EntityModel<GiftCertificate>> modelFromCertificates = certificates.stream()
                .map(order -> EntityModel.of(order,
                        linkTo(methodOn(GiftCertificateController.class).addNewCertificate(certificates.get(0)))
                                .withRel("Creates a new certificate (params: certificate): POST"),
                        linkTo(methodOn(GiftCertificateController.class).certificate(certificates.get(0).getId()))
                                .withRel("Fetches and removes certificate from the system" +
                                        " (params: certificateId): GET, DELETE")
                ))
                .collect(Collectors.toList());
        CollectionModel<EntityModel<GiftCertificate>> collectionModel = CollectionModel.of(modelFromCertificates,
                linkTo(methodOn(UserController.class).fetchAllUsers(new HashMap<String, String>()))
                        .withRel("Fetches all users: GET"),
                linkTo(methodOn(OrderController.class).fetchAllOrders())
                        .withRel("Fetches all orders: GET"),
                linkTo(methodOn(CertificateTagController.class).tags(parameters)).withRel("Fetches all tags: GET"));
        collectionModel.add(linkTo(methodOn(GiftCertificateController.class).certificates(paramsNext)).
                        withRel("Fetches NEXT PAGE of certificates: GET"),
                linkTo(methodOn(GiftCertificateController.class).certificates(paramsPrev)).
                        withRel("Fetches PREVIOUS PAGE of certificates: GET"),
                linkTo(methodOn(GiftCertificateController.class).certificates(parameters)).withSelfRel());
        return collectionModel;
    }

    /**
     * The method that realises the 'GET /certificates/{certificateId}' query.
     *
     * @param certificateId is the ID of the {@link GiftCertificate} to find.
     * @return {@link GiftCertificate} with the certificateId if such an id exists in the system.
     */
    @GetMapping(value = "/{certificateId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<GiftCertificate> certificate(@PathVariable("certificateId") long certificateId) {
        GiftCertificate giftCertificate = certificateService.findCertificateById(certificateId);
        EntityModel<GiftCertificate> certificateEntityModel
                = EntityModel.of(giftCertificate, linkTo(methodOn(GiftCertificateController.class)
                .certificate(certificateId)).withRel("Removes the certificate: DELETE"));
        certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class).addNewCertificate(new GiftCertificate()))
                .withRel("Creates new certificates (inputs: new certificate object): POST"));
        certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class).certificate(certificateId)).withSelfRel());
        return certificateEntityModel;
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
    public EntityModel<GiftCertificate> addNewCertificate(@RequestBody GiftCertificate newGiftCertificate) {
        GiftCertificate giftCertificate = certificateService.createCertificate(newGiftCertificate);
        EntityModel<GiftCertificate> certificateEntityModel
                = EntityModel.of(giftCertificate, linkTo(methodOn(GiftCertificateController.class)
                .updateGiftCertificate(giftCertificate.getId(), giftCertificate))
                .withRel("Updates the certificate (inputs: certificateId, certificate object): PUT"));
        certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class).certificate(giftCertificate.getId()))
                .withRel("Fetches and removes a certificate: GET, DELETE"));
        return certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class)
                .addNewCertificate(new GiftCertificate())).withSelfRel());
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
    public EntityModel<GiftCertificate> updateGiftCertificate(@PathVariable("certificateId") long certificateId,
                                                              @RequestBody GiftCertificate giftCertificate) {
        GiftCertificate certificate = certificateService.updateCertificate(certificateId, giftCertificate);
        EntityModel<GiftCertificate> certificateEntityModel
                = EntityModel.of(certificate, linkTo(methodOn(GiftCertificateController.class)
                .addNewCertificate(new GiftCertificate()))
                .withRel("Creates new certificates (inputs: new certificate object): POST"));
        certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class).certificate(giftCertificate.getId()))
                .withRel("Fetches and removes a certificate: GET, DELETE"));
        return certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class)
                .updateGiftCertificate(certificate.getId(), certificate)).withSelfRel());
    }
}
