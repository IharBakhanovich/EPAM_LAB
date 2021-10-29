package com.epam.esm.controller;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public CertificateTag mostPopularTagOfTheBestUser() {
        return tagService.mostPopularTagOfTheBestUser();
    }
}
