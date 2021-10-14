package com.epam.esm.controller;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API to work with {@link CertificateTag}s of the GiftCertificatesShop.
 */
@RestController
@RequestMapping("/tags")
public class CertificateTagController {
    private final TagService tagService;

    /**
     * Constructs the {@link CertificateTagController}.
     * @param tagService is the service to inject.
     */
    @Autowired
    public CertificateTagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * The method that realises the 'GET /tags' query.
     *
     * @return {@link List<CertificateTag>} - all the {@link CertificateTag} in the system.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CertificateTag> tags() {
        return tagService.findAllCertificateTags();
    }

    /**
     * The method that realises the 'GET /tags/{tagId}' query.
     *
     * @param tagId is the ID of the {@link CertificateTag} to find.
     * @return {@link CertificateTag} with the tagId if such an id exists in the system.
     */
    @GetMapping(value = "/{tagId}")
    @ResponseStatus(HttpStatus.OK)
    public CertificateTag tag(@PathVariable("tagId") long tagId) {
        return tagService.findCertificateTagById(tagId);
    }

    //    // GET /tags/byname?tag_name=12345
//    @RequestMapping(value = "/byname", method = RequestMethod.GET)
//    public CertificateTag certificateTagByName(@RequestParam("tag_name") String tagName) {
//        return tagService.findCertificateTagByName(tagName);
//    }

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
    @ResponseBody
    public CertificateTag addNewTag(@RequestBody CertificateTag certificateTag) {
        return tagService.createCertificateTag(certificateTag);
    }

    /**
     * The method that realises the 'PUT /tags/{tagId}' query and updates the {@link CertificateTag}
     * with the id equals {@param tagId}.
     *
     * @param certificateTag is the {@link CertificateTag} to update.
     * @param tagId is the id of the {@link CertificateTag}, which is to update.
     * @return the updated {@link CertificateTag}.
     */
    @PutMapping(value = "/{tagId}")
    @ResponseStatus(HttpStatus.OK)
    public CertificateTag updateCertificateTag(@PathVariable("tagId") long tagId,
                                                 @RequestBody CertificateTag certificateTag) {
        return tagService.updateCertificateTag(tagId, certificateTag);
    }
}
