package com.epam.esm.controller;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * API to work with {@link User}s of the GiftCertificatesShop.
 */
@RestController
@RequestMapping("/statistics")
public class StatisticController {
    private final TagService tagService;

    @Autowired
    public StatisticController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * The method that realises the 'GET /statisticsusers/{userId}/orders' query.
     * Returns the most popular {@link CertificateTag} of the {@link User}
     * with the biggest sum of order price.
     *
     * @return {@link CertificateTag}.
     */
    @GetMapping(value = "/mostPopularTag")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<CertificateTag> mostPopularTagOfTheBestUser() {
        CertificateTag tag = tagService.mostPopularTagOfTheBestUser();

        EntityModel<CertificateTag> orderEntityModel
                = EntityModel.of(tag, linkTo(methodOn(CertificateTagController.class)
                .tag(tag.getId())).withRel("Fetches tag by tagId(inputs: tagId): GET"));
        orderEntityModel.add(linkTo(methodOn(CertificateTagController.class).addNewTag(new CertificateTag()))
                .withRel("Creates new tag (inputs: new Tag object): POST"));
        orderEntityModel.add(linkTo(methodOn(CertificateTagController.class).tags())
                .withRel("Fetches all tags: GET"));

        return orderEntityModel.add(linkTo(methodOn(StatisticController.class)
                .mostPopularTagOfTheBestUser()).withSelfRel());
    }
}
