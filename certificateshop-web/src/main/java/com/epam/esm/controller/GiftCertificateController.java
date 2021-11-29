package com.epam.esm.controller;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    private final Translator translator;

    /**
     * Constructs the {@link GiftCertificateController}.
     *
     * @param certificateService is the service to inject.
     */
    @Autowired
    public GiftCertificateController(CertificateService certificateService, Translator translator) {
        this.certificateService = certificateService;
        this.translator = translator;
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
        parameters = ColumnNames.validateParameters(parameters, ColumnNames.DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
        List<GiftCertificate> certificates = certificateService.findAllCertificates(parameters);
        int pageNumber = Integer.parseInt(parameters.get(ColumnNames.PAGE_NUMBER_PARAM_NAME));
        int amountEntitiesOnThePage
                = Integer.parseInt(parameters.get(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME));
        Map<String, String> paramsNext
                = ColumnNames.createNextParameters(certificates, pageNumber, amountEntitiesOnThePage);
        Map<String,String> paramsPrev
                = ColumnNames.createPrevParameters(certificates, pageNumber, amountEntitiesOnThePage);

        List<EntityModel<GiftCertificate>> modelFromCertificates = certificates.stream()
                .map(order -> EntityModel.of(order,
                        linkTo(methodOn(GiftCertificateController.class).addNewCertificate(certificates.get(0)))
                                .withRel(translator.toLocale("CREATES_NEW_CERTIFICATE_HATEOAS_LINK_MESSAGE")),
                        linkTo(methodOn(GiftCertificateController.class).certificate(certificates.get(0).getId()))
                                .withRel(translator
                                        .toLocale("FETCHES_AND_REMOVES_CERTIFICATE_HATEOAS_LINK_MESSAGE"))))
                .collect(Collectors.toList());
        CollectionModel<EntityModel<GiftCertificate>> collectionModel = CollectionModel.of(modelFromCertificates,
                linkTo(methodOn(UserController.class).fetchAllUsers(ColumnNames.DEFAULT_PARAMS))
                        .withRel(translator.toLocale("FETCHES_ALL_USERS_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(OrderController.class).fetchAllOrders(ColumnNames.DEFAULT_PARAMS))
                        .withRel(translator.toLocale("FETCHES_ALL_ORDERS_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(CertificateTagController.class).tags(parameters))
                        .withRel(translator.toLocale("FETCHES_ALL_TAGS_HATEOAS_LINK_MESSAGE")));
        collectionModel.add(linkTo(methodOn(GiftCertificateController.class).certificates(paramsNext)).
                        withRel(translator.toLocale("FETCHES_NEXT_PAGE_CERTIFICATES_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(GiftCertificateController.class).certificates(paramsPrev)).
                        withRel(translator.toLocale("FETCHES_PREVIOUS_PAGE_CERTIFICATES_HATEOAS_LINK_MESSAGE")),
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
        EntityModel<GiftCertificate> certificateEntityModel = EntityModel.of(giftCertificate,
                linkTo(methodOn(GiftCertificateController.class).certificate(certificateId))
                .withRel(translator.toLocale("REMOVES_CERTIFICATE_HATEOAS_LINK_MESSAGE")));
        certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class)
                .addNewCertificate(new GiftCertificate()))
                .withRel(translator.toLocale("CREATES_NEW_CERTIFICATE_HATEOAS_LINK_MESSAGE")));
        certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class)
                .certificate(certificateId)).withSelfRel());
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
                .withRel(translator.toLocale("UPDATES_NEW_CERTIFICATE_HATEOAS_LINK_MESSAGE")));
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
                .withRel(translator.toLocale("CREATES_NEW_CERTIFICATE_HATEOAS_LINK_MESSAGE")));
        certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class).certificate(giftCertificate.getId()))
                .withRel(translator.toLocale("FETCHES_AND_REMOVES_CERTIFICATE_HATEOAS_LINK_MESSAGE")));
        return certificateEntityModel.add(linkTo(methodOn(GiftCertificateController.class)
                .updateGiftCertificate(certificate.getId(), certificate)).withSelfRel());
    }
}
