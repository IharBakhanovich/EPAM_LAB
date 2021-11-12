package com.epam.esm.service.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Transactional
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
        giftCertificate.setId(0);
        giftCertificate.setCreateDate(LocalDateTime.now());
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        saveNewTagsInDatabase(giftCertificate, 0);
        certificateDAO.save(giftCertificate);
        saveRelations(giftCertificate);
        return certificateDAO.findByName(giftCertificate.getName()).get();
    }

    // if IdCertificateFromDB is 0, that means that it is for creatinf a new certificate, otherwise for updating
    private void saveNewTagsInDatabase(GiftCertificate giftCertificate, long idCertificateFromDB) {
        List<CertificateTag> certificateTags = giftCertificate.getTags();
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        if (certificateTags != null) {
            for (CertificateTag certificateTag : fetchTagsWithUniqueNames(certificateTags)) {
                Optional<CertificateTag> certificateTag1 = tagDAO.findByName(certificateTag.getName());
                // to check if the tag is new, or it already exists in the database
                if (!certificateTag1.isPresent()) {
                    CertificateTag newTag = new CertificateTag(0, certificateTag.getName());
                    tagValidator.validateTag(newTag, true);
                    tagDAO.save(newTag);
                    certificateTags1.add(tagDAO.findByName(newTag.getName()).get());
                } else {
                    // to save an existed tag in the new list
                    if (!certificateTags1.contains(certificateTag1.get())) {
                        certificateTags1.add(certificateTag1.get());
                    }
                }
            }
        } else {
            if (idCertificateFromDB != 0) {
                certificateTags1 = certificateDAO.findById(idCertificateFromDB).get().getTags();
            }
        }
        giftCertificate.setTags(certificateTags1);
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
     *
     * @param parameters the filters and parameters to apply to the values to be returned.
     * @return {@link List<GiftCertificate>}.
     */
    @Override
    public List<GiftCertificate> findAllCertificates(Map<String, String> parameters) {
        List<String> errorMessage = new ArrayList<>();
        int pageNumber = Integer.parseInt(parameters.get(ColumnNames.PAGE_NUMBER_PARAM_NAME));
        int amountEntitiesOnThePage
                = Integer.parseInt(parameters.get(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME));
        checkLimitAndOffset(errorMessage, pageNumber, amountEntitiesOnThePage);
        List<GiftCertificate> giftCertificates = new ArrayList<>();
        giftCertificates = certificateDAO.findAllPagination(pageNumber, amountEntitiesOnThePage, parameters);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            giftCertificates = Arrays.stream(HandlerType.values())
                    .filter(handlerType -> handlerType.getParameterName().equals(parameter.getKey()))
                    .findAny().orElseThrow(RuntimeException::new)
                    .handle(giftCertificates, parameter.getValue());
        }
        return giftCertificates;
    }

    private void checkLimitAndOffset(List<String> errorMessage, int pageNumber, int amountEntitiesOnThePage) {
        if (pageNumber < 0) {
            errorMessage.add(translator.toLocale("THE_PAGE_NUMBER_SHOULD_BE_MORE_THAN_0"));
        }
        if (amountEntitiesOnThePage < 0) {
            errorMessage.add(translator.toLocale("THE_AMOUNT_ENTITIES_ON_THE_PAGE_SHOULD_BE_MORE_THAN_0"));
        }
        if (!errorMessage.isEmpty()) {
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_TAG_NOT_VALID, errorMessage);
        }
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
        saveNewTagsInDatabase(giftCertificate, giftCertificateFromDB.getId());
        for (CertificateTag tag : giftCertificate.getTags()) {
            tagValidator.validateTag(tag, true);
        }
        giftCertificate = fillCertificateValues(giftCertificate, giftCertificateFromDB);
        certificateValidator.validateCertificate(giftCertificate, false);
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

    private GiftCertificate fillCertificateValues(GiftCertificate giftCertificate, GiftCertificate giftCertificateFromDB) {
        giftCertificate.setId(giftCertificateFromDB.getId());
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
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        return new GiftCertificate(giftCertificate.getId(), giftCertificate.getName(),
                giftCertificate.getDescription(), giftCertificate.getPrice(), giftCertificate.getDuration(),
                giftCertificate.getCreateDate(), LocalDateTime.now(), giftCertificate.getTags());
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
