package com.epam.esm.service.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.handler.HandlerType;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.TagValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CertificateServiceImpl implements CertificateService {
    public static final String ERROR_CODE_DUPLICATE = "409";
    public static final String ERROR_CODE_ENTITY_NOT_FOUND = "404";
    public static final String ERROR_CODE_METHOD_ARGUMENT_NOT_VALID = "400";
    public static final String ERROR_CODE_CERTIFICATE_NOT_VALID = "01";
    public static final String ERROR_CODE_TAG_NOT_VALID = "02";

    private final CertificateDao certificateDAO;
    private final TagDao tagDAO;
    private final CertificateValidator certificateValidator;
    private final TagValidator tagValidator;
    private final Translator translator;

    @Autowired
    public CertificateServiceImpl(CertificateDao certificateDAO, TagDao tagDAO, CertificateValidator certificateValidator, TagValidator tagValidator, Translator translator) {
        this.certificateDAO = certificateDAO;
        this.tagDAO = tagDAO;
        this.certificateValidator = certificateValidator;
        this.tagValidator = tagValidator;
        this.translator = translator;
    }

    /**
     * Creates a new {@link GiftCertificate} in the system.
     *
     * @param giftCertificate is the {@link GiftCertificate} to create.
     * @throws DuplicateException if there is the user with the such a nickname in the system.
     */
    @Override
    public GiftCertificate createCertificate(GiftCertificate giftCertificate) {
        if (certificateDAO.findByName(giftCertificate.getName()).isPresent()) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(String.format(translator
                    .toLocale("CERTIFICATE_WITH_SUCH_NAME_EXIST_IN_DB_MESSAGE"), giftCertificate.getName()));
            throw new DuplicateException(ERROR_CODE_DUPLICATE + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
        }
        certificateValidator.validateCertificate(giftCertificate, true);
        List<String> errorMessage = new ArrayList<>();
        // tag validation
        for (CertificateTag certificateTag : giftCertificate.getTags()) {
            if (!tagValidator.isNameValid(certificateTag.getName(), 30)) {
                errorMessage.add(certificateTag
                        + ": " + translator.toLocale("TAG_NAME_IS_NOT_VALID_ERROR_MESSAGE"));
            }
        }
        if (!errorMessage.isEmpty()) {
            throw new MethodArgumentNotValidException(ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_TAG_NOT_VALID,
                    errorMessage);
        }
        giftCertificate.setCreateDate(LocalDateTime.now());
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        certificateDAO.save(giftCertificate);
        saveNewTagsInDatabase(giftCertificate);
        saveRelations(giftCertificate);
        return certificateDAO.findByName(giftCertificate.getName()).get();
    }

    private void saveNewTagsInDatabase(GiftCertificate giftCertificate) {
        List<CertificateTag> certificateTags = giftCertificate.getTags();
        for (CertificateTag certificateTag : certificateTags) {
            if (!tagDAO.findByName(certificateTag.getName()).isPresent()) {
                tagDAO.save(certificateTag);
            }
        }
    }

    private void saveRelations(GiftCertificate giftCertificate) {
        List<CertificateTag> allTagsInDB = tagDAO.findAll();
        Optional<GiftCertificate> certificate
                = certificateDAO.findByName((giftCertificate.getName()));
        long newGiftCertificateId = certificate.get().getId();
        for (CertificateTag certificateTag : fetchTagsWithUniqueNames(giftCertificate.getTags())) {
            for (CertificateTag certificateTagFromDB : allTagsInDB) {
                if (certificateTag.getName().equals(certificateTagFromDB.getName())) {
                    certificateDAO.saveIdsInHas_tagTable(newGiftCertificateId, certificateTagFromDB.getId());
                }
            }
        }
    }

    private List<CertificateTag> fetchTagsWithUniqueNames(List<CertificateTag> tags) {
        List<CertificateTag> uniqueTags = new ArrayList<>();
        tags.stream()
                .filter(certificateTag -> uniqueTags.stream()
                        .noneMatch(certificateTag1 -> certificateTag1.getName().equals(certificateTag.getName())))
                .forEach(uniqueTags::add);
        return uniqueTags;
    }

    /**
     * Returns all certificates in the system.
     */
    @Override
    public List<GiftCertificate> findAllCertificates(Map<String, String> parameters) {
        List<GiftCertificate> giftCertificates = certificateDAO.findAll();
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            giftCertificates = Arrays.stream(HandlerType.values())
                    .filter(handlerType -> handlerType.getParameterName().equals(parameter.getKey()))
                    .findAny().orElseThrow(RuntimeException::new)
                    .handle(giftCertificates, parameter.getValue());
        }
        return giftCertificates;
    }

    /**
     * Returns a certificate by its id.
     *
     * @param id is the id of the {@link GiftCertificate} to find in the system.
     */
    @Override
    public GiftCertificate findCertificateById(long id) {
        return checkAndGetGiftCertificateById(id).get();
    }

    private Optional<GiftCertificate> checkAndGetGiftCertificateById(long id) {
        List<String> errorMessage = new ArrayList<>();
        if (id < 0) {
            errorMessage.add(translator.toLocale("THE_ID_SHOULD_NOT_BE_LESS_THAN_0"));
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
        }
        Optional<GiftCertificate> certificate = certificateDAO.findById(id);
        if (!certificate.isPresent()) {
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_A_CERTIFICATE_WITH_SUCH_AN_ID_IN_DATABASE"), id));
            throw new EntityNotFoundException(
                    ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
        }
        return certificate;
    }

    /**
     * Updates a certificate.
     *
     * @param giftCertificate is the {@link GiftCertificate} to update.
     * @throws EntityNotFoundException if there is no such an certificate in the system.
     */
    @Override
    public GiftCertificate updateCertificate(long id, GiftCertificate giftCertificate) {
        GiftCertificate giftCertificateFromDB;
        checkGiftCertificateById(id);
        giftCertificateFromDB = fetchAndCheckGiftCertificateFromDB(giftCertificate, id);
        fillValidateAndUpdateGiftCertificate(giftCertificate, giftCertificateFromDB);
        return certificateDAO.findById(id).get();
    }

    private GiftCertificate fetchAndCheckGiftCertificateFromDB(GiftCertificate giftCertificate, long id) {
        GiftCertificate giftCertificateFromDB;
        giftCertificateFromDB = certificateDAO.findById(id).get();
        if (giftCertificate.getName() != null) {
            List<String> errorMessage = new ArrayList<>();
            if (certificateDAO.findByName(giftCertificate.getName()).isPresent()
                    && !giftCertificate.getName().equals(giftCertificateFromDB.getName())) {
                errorMessage.add(String.format(translator
                        .toLocale("CERTIFICATE_WITH_SUCH_NAME_EXIST_IN_DB_MESSAGE"), giftCertificate.getName()));
                throw new DuplicateException(ERROR_CODE_DUPLICATE + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
            }
        }
        return giftCertificateFromDB;
    }

    private void fillValidateAndUpdateGiftCertificate(GiftCertificate giftCertificate, GiftCertificate giftCertificateFromDB) {
        fillCertificateValues(giftCertificate, giftCertificateFromDB);
        certificateValidator.validateCertificate(giftCertificate, false);
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        giftCertificate.setId(giftCertificateFromDB.getId());
        certificateDAO.update(giftCertificate);
        updateHasTagTable(giftCertificate, giftCertificateFromDB);
    }

    private void updateHasTagTable(GiftCertificate giftCertificate, GiftCertificate giftCertificateFromDB) {
        List<CertificateTag> certificateTagsToUpdate = fetchTagsWithUniqueNames(giftCertificate.getTags());
        for (CertificateTag certificateTag : certificateTagsToUpdate) {
            tagValidator.validateTag(certificateTag, true);
            if (tagDAO.findByName(certificateTag.getName()).isPresent()) {
                boolean isExistByGiftCertificate = false;
                isExistByGiftCertificate
                        = checkByNameIfCertificateTagExist(
                        giftCertificateFromDB, certificateTag, isExistByGiftCertificate);
                if (!isExistByGiftCertificate) {
                    long idOfTag = tagDAO.findByName(certificateTag.getName()).get().getId();
                    certificateDAO.saveIdsInHas_tagTable(giftCertificate.getId(), idOfTag);
                }
            } else {
                tagDAO.save(certificateTag);
                CertificateTag newTag = tagDAO.findByName(certificateTag.getName()).get();
                certificateDAO.saveIdsInHas_tagTable(giftCertificate.getId(), newTag.getId());
            }
        }
        deleteTagsWhichPresentInCertificateFromDBButNotPresentInTagsToUpdate(
                giftCertificate, giftCertificateFromDB, certificateTagsToUpdate);
    }

    private void deleteTagsWhichPresentInCertificateFromDBButNotPresentInTagsToUpdate(
            GiftCertificate giftCertificate,
            GiftCertificate giftCertificateFromDB,
            List<CertificateTag> certificateTagsToUpdate) {
        for (CertificateTag certificateTag : giftCertificateFromDB.getTags()) {
            boolean isPresentInCertificateTagsToUpdate = false;
            isPresentInCertificateTagsToUpdate
                    = checkByNameIfCertificateTagIsPresent(
                    certificateTagsToUpdate, certificateTag, isPresentInCertificateTagsToUpdate);
            if (!isPresentInCertificateTagsToUpdate) {
                certificateDAO.deleteIdsInHas_TagTable(giftCertificate.getId(), certificateTag.getId());
            }
        }
    }

    private boolean checkByNameIfCertificateTagExist(
            GiftCertificate giftCertificateFromDB, CertificateTag certificateTag, boolean isExistByGiftCertificate) {
        if (giftCertificateFromDB.getTags()
                .stream().anyMatch(certificateTagOfGiftCertificateFromDB -> certificateTagOfGiftCertificateFromDB
                        .getName().equals(certificateTag.getName()))) {
            isExistByGiftCertificate = true;
        }
        return isExistByGiftCertificate;
    }

    private boolean checkByNameIfCertificateTagIsPresent(
            List<CertificateTag> certificateTagsToUpdate,
            CertificateTag certificateTag,
            boolean isPresentInCertificateTagsToUpdate) {
        if (certificateTagsToUpdate.stream()
                .anyMatch(certificateTag1 -> certificateTag1.getName().equals(certificateTag.getName()))) {
            isPresentInCertificateTagsToUpdate = true;
        }
        return isPresentInCertificateTagsToUpdate;
    }

    private void fillCertificateValues(GiftCertificate giftCertificate, GiftCertificate giftCertificateFromDB) {
        if (giftCertificate.getName() == null) {
            giftCertificate.setName(giftCertificateFromDB.getName());
        }
        if (giftCertificate.getDescription() == null) {
            giftCertificate.setDescription(giftCertificateFromDB.getDescription());
        }
        if (giftCertificate.getPrice() == null) {
            giftCertificate.setPrice(giftCertificateFromDB.getPrice());
        }
        if (giftCertificate.getDuration() == 0) {
            giftCertificate.setDuration(giftCertificateFromDB.getDuration());
        }
        if (giftCertificate.getTags() == null) {
            giftCertificate.setTags(giftCertificateFromDB.getTags());
        }
        giftCertificate.setCreateDate(giftCertificateFromDB.getCreateDate());
        giftCertificate.setLastUpdateDate(giftCertificateFromDB.getLastUpdateDate());
    }

    /**
     * Deletes a certificate by its id.
     *
     * @param id is the id of the {@link GiftCertificate} to delete.
     */
    @Override
    public void deleteCertificate(long id) {
        GiftCertificate giftCertificate = checkGiftCertificateById(id);
        if (giftCertificate.getTags().size() != 0) {
            updateHas_TagTable(giftCertificate);
        }
        certificateDAO.delete(id);
    }

    private void updateHas_TagTable(GiftCertificate giftCertificate) {
        giftCertificate.getTags()
                .forEach(certificateTag -> certificateDAO
                        .deleteIdsInHas_TagTable(giftCertificate.getId(), certificateTag.getId()));
    }

    private GiftCertificate checkGiftCertificateById(long id) {
        List<String> errorMessage = new ArrayList<>();
        if (id < 0) {
            errorMessage.add(translator.toLocale("THE_ID_SHOULD_NOT_BE_LESS_THAN_0"));
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
        }
        Optional<GiftCertificate> certificate = certificateDAO.findById(id);
        if (!certificate.isPresent()) {
            errorMessage.add(String.format(translator.
                    toLocale("THERE_IS_NO_A_CERTIFICATE_WITH_SUCH_AN_ID_IN_DATABASE"), id));
            throw new EntityNotFoundException(
                    ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
        }
        return certificate.get();
    }

    /**
     * Returns a certificate by its name.
     *
     * @param name is the name of the {@link GiftCertificate} to find in the system.
     */
    @Override
    public GiftCertificate findCertificateByName(String name) {
        Optional<GiftCertificate> certificate = fetchAndCheckCertificateByName(name);
        return certificate.get();
    }

    private Optional<GiftCertificate> fetchAndCheckCertificateByName(String name) {
        List<String> errorMessage = new ArrayList<>();
        Optional<GiftCertificate> certificate = certificateDAO.findByName(name);
        if (!certificate.isPresent()) {
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_A_CERTIFICATE_WITH_SUCH_A_NAME_IN_DATABASE"), name));
            throw new EntityNotFoundException(
                    ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
        }
        return certificate;
    }
}
