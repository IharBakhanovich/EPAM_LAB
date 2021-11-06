package com.epam.esm.controller;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.service.TagService;
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
 * API to work with {@link CertificateTag}s of the GiftCertificatesShop.
 */
@RestController
@RequestMapping("/tags")
public class CertificateTagController {
    private final TagService tagService;
    private final Translator translator;

    /**
     * Constructs the {@link CertificateTagController}.
     *
     * @param tagService is the service to inject.
     */
    @Autowired
    public CertificateTagController(TagService tagService, Translator translator) {
        this.tagService = tagService;
        this.translator = translator;
    }

    /**
     * The method that realises the 'GET /tags' query.
     *
     * @return {@link List<CertificateTag>} - all the {@link CertificateTag} in the system.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<EntityModel<CertificateTag>> tags(@RequestParam Map<String, String> parameters) {
        parameters = ColumnNames.validateParameters(parameters, ColumnNames.DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
        List<CertificateTag> tags = tagService.findAllCertificateTags(parameters);

        int pageNumber = Integer.parseInt(parameters.get(ColumnNames.PAGE_NUMBER_PARAM_NAME));
        int amountEntitiesOnThePage
                = Integer.parseInt(parameters.get(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME));
        Map<String, String> paramsNext = ColumnNames.createNextParameters(tags, pageNumber, amountEntitiesOnThePage);
        Map<String,String> paramsPrev = ColumnNames.createPrevParameters(tags, pageNumber, amountEntitiesOnThePage);
        List<EntityModel<CertificateTag>> modelFromOrders = tags.stream().map(tag -> EntityModel.of(tag,
                        linkTo(methodOn(CertificateTagController.class).tag(tag.getId()))
                                .withRel(translator.toLocale("FETCHES_AND_REMOVES_TAG_HATEOAS_LINK_MESSAGE")),
                        linkTo(methodOn(CertificateTagController.class).updateCertificateTag(tag.getId(), tag))
                                .withRel(translator.toLocale("UPDATES_TAG_HATEOAS_LINK_MESSAGE"))))
                .collect(Collectors.toList());
        CollectionModel<EntityModel<CertificateTag>> collectionModel = CollectionModel.of(modelFromOrders);
        collectionModel.add(linkTo(methodOn(CertificateTagController.class).tags(paramsNext)).
                        withRel(translator.toLocale("FETCHES_NEXT_PAGE_TAG_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(GiftCertificateController.class).certificates(paramsPrev)).
                        withRel(translator.toLocale("FETCHES_PREVIOUS_PAGE_TAG_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(GiftCertificateController.class).certificates(parameters)).withSelfRel());
        return collectionModel;
    }

    /**
     * The method that realises the 'GET /tags/{tagId}' query.
     *
     * @param tagId is the ID of the {@link CertificateTag} to find.
     * @return {@link CertificateTag} with the tagId if such an id exists in the system.
     */
    @GetMapping(value = "/{tagId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<CertificateTag> tag(@PathVariable("tagId") long tagId) {
        CertificateTag tag = tagService.findCertificateTagById(tagId);
        EntityModel<CertificateTag> orderEntityModel
                = EntityModel.of(tag, linkTo(methodOn(StatisticController.class)
                .mostPopularTagOfTheBestUser())
                .withRel(translator.toLocale(
                        "FETCHES_MOST_POPULAR_TAG_USER_WITH_HIGHEST_ORDERS_SUM_HATEOAS_LINK_MESSAGE")));
        orderEntityModel.add(linkTo(methodOn(CertificateTagController.class).addNewTag(new CertificateTag()))
                .withRel(translator.toLocale("CREATES_NEW_TAG_HATEOAS_LINK_MESSAGE")));
        orderEntityModel.add(linkTo(methodOn(CertificateTagController.class).tag(tag.getId()))
                .withRel(translator.toLocale("REMOVES_TAG_HATEOAS_LINK_MESSAGE")));
        return orderEntityModel.add(linkTo(methodOn(CertificateTagController.class).tag(tag.getId())).withSelfRel());
    }

    /**
     * The method that realises the 'DELETE /tags/{tagId}' query.
     *
     * @param tagId is the ID of the {@link CertificateTag} to delete.
     */
    @DeleteMapping(value = "/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable("tagId") long tagId) {
        tagService.deleteCertificateTag(tagId);
    }

    /**
     * The method that realises the 'POST /tags' query.
     *
     * @param certificateTag is the {@link CertificateTag} to create.
     * @return the created {@link CertificateTag}.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<CertificateTag> addNewTag(@RequestBody CertificateTag certificateTag) {
        CertificateTag tag = tagService.createCertificateTag(certificateTag);
        EntityModel<CertificateTag> orderEntityModel
                = EntityModel.of(tag, linkTo(methodOn(CertificateTagController.class)
                .updateCertificateTag(tag.getId(), tag))
                .withRel(translator.toLocale("UPDATES_TAG_HATEOAS_LINK_MESSAGE")));
        orderEntityModel.add(linkTo(methodOn(CertificateTagController.class).tag(tag.getId()))
                .withRel(translator.toLocale("FETCHES_AND_REMOVES_TAG_HATEOAS_LINK_MESSAGE")));
        return orderEntityModel.add(linkTo(methodOn(CertificateTagController.class).addNewTag(new CertificateTag()))
                .withSelfRel());
    }

    /**
     * The method that realises the 'PUT /tags/{tagId}' query and updates the {@link CertificateTag}
     * with the id equals {@param tagId}.
     *
     * @param certificateTag is the {@link CertificateTag} to update.
     * @param tagId          is the id of the {@link CertificateTag}, which is to update.
     * @return the updated {@link CertificateTag}.
     */
    @PutMapping(value = "/{tagId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<CertificateTag> updateCertificateTag(@PathVariable("tagId") long tagId,
                                                            @RequestBody CertificateTag certificateTag) {

        CertificateTag tag = tagService.updateCertificateTag(tagId, certificateTag);
        EntityModel<CertificateTag> orderEntityModel
                = EntityModel.of(tag, linkTo(methodOn(CertificateTagController.class)
                .tag(tagId)).withRel(translator.toLocale("FETCHES_AND_REMOVES_TAG_HATEOAS_LINK_MESSAGE")));
        orderEntityModel.add(linkTo(methodOn(CertificateTagController.class).addNewTag(new CertificateTag()))
                .withRel(translator.toLocale("CREATES_NEW_TAG_HATEOAS_LINK_MESSAGE")));
        return orderEntityModel.add(linkTo(methodOn(CertificateTagController.class)
                .updateCertificateTag(tagId, certificateTag)).withSelfRel());
    }
}
