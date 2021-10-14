package com.epam.esm.controller;

import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.service.CertificateService;
import org.apache.maven.shared.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
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
     * @return {@link List<GiftCertificate>} - all the {@link GiftCertificate} in the system.
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
     * The method that realises the 'PUT /certificates/{certificateId}' query and updates the {@link GiftCertificate}
     * with the id equals {@param certificateId}.
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

//    /**
//     * The method that realises the 'GET /certificates/search?tag_mane=123&part_cert_name=123&part_descr_name=123&sortByName=true&sortByDate=false
//     * and returns the {@link List<GiftCertificate>}.
//     *
//     * @param tagName the name of the Tag that all {@link GiftCertificate} should contain (optional).
//     * @param partSertName the part of the name, that all the {@link GiftCertificate} should contain (optional).
//     * @param partDescrName the part of the description, that all the {@link GiftCertificate} should contain (optional).
//     * @param sortByName if true sorts ASC by {@link GiftCertificate} name
//     *                  and if false sorts DESC by {@link GiftCertificate} name (optional).
//     * @param sortByDate if true sorts ASC by {@link GiftCertificate} createDate
//     *                   and if false sorts DESC by {@link GiftCertificate} createDate.
//     * @return the {@link List<GiftCertificate>}.
//     */
//    @GetMapping(value = "/search")
//    public List<GiftCertificate> certificateByName(@RequestParam(required = false, name = "tag_name") String tagName,
//                                                   @RequestParam(required = false, name = "part_cert_name") String partSertName,
//                                                   @RequestParam(required = false, name = "part_descr_name") String partDescrName,
//                                                   @RequestParam(required = false, name = "sortByName") Boolean sortByName,
//                                                   @RequestParam(required = false, name = "sortByDate") Boolean sortByDate) {
//        List<GiftCertificate> certificates = fetchCertificatesByParams(tagName, partSertName, partDescrName);
//        sortCertificates(sortByName, sortByDate, certificates);
//        return certificates;
//
//    }
//
//    private void sortCertificates(Boolean sortByName, Boolean sortByDate, List<GiftCertificate> certificates) {
//        if (sortByName != null && sortByName && sortByDate != null && sortByDate) {
//            certificates
//                    .sort(Comparator.comparing(GiftCertificate::getName)
//                            .thenComparing(GiftCertificate::getCreateDate));
//        }
//
//        if (sortByName != null && !sortByName && sortByDate != null && sortByDate) {
//            certificates
//                    .sort(Comparator.comparing(GiftCertificate::getName).reversed()
//                            .thenComparing(GiftCertificate::getCreateDate));
//        }
//        if (sortByName != null && !sortByName && sortByDate != null && !sortByDate) {
//            certificates
//                    .sort(Comparator.comparing(GiftCertificate::getName).reversed()
//                            .thenComparing(GiftCertificate::getCreateDate).reversed());
//        }
//
//        if (sortByName == null && sortByDate != null && !sortByDate) {
//            certificates
//                    .sort(Comparator.comparing(GiftCertificate::getCreateDate).reversed());
//        }
//
//        if (sortByName == null && sortByDate != null && sortByDate) {
//            certificates
//                    .sort(Comparator.comparing(GiftCertificate::getCreateDate));
//        }
//
//        if (sortByName != null && sortByName && sortByDate == null) {
//            certificates
//                    .sort(Comparator.comparing(GiftCertificate::getName));
//        }
//
//        if (sortByName != null && !sortByName && sortByDate == null) {
//            certificates
//                    .sort(Comparator.comparing(GiftCertificate::getName).reversed());
//        }
//    }
//
//    private List<GiftCertificate> fetchCertificatesByParams(String tagName, String partSertName, String partDescrName) {
//        List<GiftCertificate> certificates = new ArrayList<>();
//        if (!StringUtils.isEmpty(tagName) && StringUtils.isEmpty(partSertName) && StringUtils.isEmpty(partDescrName)) {
//            certificates = certificateService.findCertificateByTagName(tagName);
//        }
//
//        if (StringUtils.isEmpty(tagName) && !StringUtils.isEmpty(partSertName) && StringUtils.isEmpty(partDescrName)) {
//            certificates = certificateService.findCertificatesByPartSertName(partSertName);
//        }
//
//        if (StringUtils.isEmpty(tagName) && StringUtils.isEmpty(partSertName) && !StringUtils.isEmpty(partDescrName)) {
//            certificates = certificateService.findCertificatesByPartDescrName(partDescrName);
//        }
//
//        if (!StringUtils.isEmpty(tagName) && !StringUtils.isEmpty(partSertName) && StringUtils.isEmpty(partDescrName)) {
//            certificates = certificateService.findCertificatesByTagNameAndPartSertName(tagName, partSertName);
//        }
//
//        if (StringUtils.isEmpty(tagName) && !StringUtils.isEmpty(partSertName) && !StringUtils.isEmpty(partDescrName)) {
//            certificates
//                    = certificateService.findCertificatesByPartSertNameAndPartDescrName(partSertName, partDescrName);
//        }
//
//        if (!StringUtils.isEmpty(tagName) && StringUtils.isEmpty(partSertName) && !StringUtils.isEmpty(partDescrName)) {
//            certificates = certificateService.findCertificatesByTagNameAndPartDescrName(tagName, partDescrName);
//        }
//
//        if (!StringUtils.isEmpty(tagName) && !StringUtils.isEmpty(partSertName) && !StringUtils.isEmpty(partDescrName)) {
//            certificates = certificateService.findCertificatesByAllParameters(tagName, partSertName, partDescrName);
//        }
//
//        if (StringUtils.isEmpty(tagName) && StringUtils.isEmpty(partSertName) && StringUtils.isEmpty(partDescrName)) {
//            certificates = certificateService.findAllCertificates(filters);
//        }
//        return certificates;
//    }

//    // GET /certificates/{certificateId}/tags
//    @RequestMapping(value = "/{certificateId}/tags", method = RequestMethod.GET)
//    public List<CertificateTag> certificateTags(@PathVariable("certificateId") long certificateId) {
//        return certificateService.findCertificateById(certificateId).getTags();
//    }

//    // DELETE /certificates/{certificateId}/tags/
//    @RequestMapping(value = "/{certificateId}/tags", method = RequestMethod.DELETE)
//    public void deleteCertificateTags(@PathVariable("certificateId") long certificateId) {
//        certificateService.deleteAllTagsCertificate(certificateId);
//    }

//    // DELETE /certificates/{certificateId}/tags/{tagId}
//    @RequestMapping(value = "/{certificateId}/tags/{tagId}", method = RequestMethod.DELETE)
//    public void deleteCertificateTag(@PathVariable("certificateId") long certificateId,
//                                     @PathVariable("tagId") long tagId) {
//        certificateService.deleteTagOfCertificate(certificateId, tagId);
//    }

//    // POST /certificates/{certificateId}/tags/{tagId}
//    @RequestMapping(value = "/{certificateId}/tags/{tagId}", method = RequestMethod.POST)
//    public GiftCertificate addCertificateTagToGiftCertificate(@PathVariable("certificateId") long certificateId,
//                                     @PathVariable("tagId") long tagId) {
//        return certificateService.addTagToCertificate(certificateId, tagId);
//    }

//    // POST /certificates/{certificateId}/tags/
//    @RequestMapping(value = "/{certificateId}/tags/{tagId}", method = RequestMethod.POST)
//    public GiftCertificate addCertificateTagToGiftCertificate(@PathVariable("certificateId") long certificateId,
//                                                              @RequestBody CertificateTag certificateTag) {
//        return certificateService.addNewTagToCertificate(certificateId, certificateTag);
//    }

//    // PUT /certificates/{name}
//    @RequestMapping(value = "/{name}", method = RequestMethod.PUT)
//    public GiftCertificate addCertificateTagToGiftCertificate(@PathVariable("name") String certificateName,
//                                                              @RequestBody GiftCertificate giftCertificate) {
//        return certificateService.updateCertificateByName(certificateName, giftCertificate);
//    }
}