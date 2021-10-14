package com.epam.esm.service.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.dao.TagDAO;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.TagValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.epam.esm.service.handler.HandlerType;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CertificateServiceImpl implements CertificateService {
    public static final String ERROR_CODE_DUPLICATE = "400";
    public static final String ERROR_CODE_ENTITY_NOT_FOUND = "404";
    public static final String ERROR_CODE_METHOD_ARGUMENT_NOT_VALID = "400";
    public static final String ERROR_CODE_CERTIFICATE_NOT_VALID = "01";
    public static final String ERROR_CODE_TAG_NOT_VALID = "02";

    private final CertificateDAO certificateDAO;
    private final TagDAO tagDAO;
    private final CertificateValidator certificateValidator;
    private final TagValidator tagValidator;
    private final Translator translator;

    @Autowired
    public CertificateServiceImpl(CertificateDAO certificateDAO, TagDAO tagDAO, CertificateValidator certificateValidator, TagValidator tagValidator, Translator translator) {
        this.certificateDAO = certificateDAO;
        this.tagDAO = tagDAO;
        this.certificateValidator = certificateValidator;
        this.tagValidator = tagValidator;
        this.translator = translator;
    }

    /**
     * Creates a new user in the system.
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
        for (CertificateTag certificateTag : giftCertificate.getTags()
        ) {
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
        for (CertificateTag certificateTag : certificateTags
        ) {
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
        for (CertificateTag certificateTag : giftCertificate.getTags()
        ) {
            for (CertificateTag certificateTagFromDB : allTagsInDB
            ) {
                if (certificateTag.getName().equals(certificateTagFromDB.getName())) {
                    certificateDAO.saveIdsInHas_tagTable(newGiftCertificateId, certificateTagFromDB.getId());
                }
            }
        }
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

    private void updateHasTagTable(GiftCertificate giftCertificate, GiftCertificate giftCertificateFromDB) {
        List<CertificateTag> allTags = tagDAO.findAll();
        List<CertificateTag> certificateTagsToUpdate = giftCertificate.getTags();
        for (CertificateTag certificateTag : certificateTagsToUpdate
        ) {
            if (!allTags.contains(certificateTag)) {
                tagValidator.validateTag(certificateTag, true);
                if (tagDAO.findByName(certificateTag.getName()).isPresent()) {
                    List<String> errorMessage = new ArrayList<>();
                    errorMessage.add(String.format(translator
                            .toLocale("TAG_WITH_SUCH_NAME_EXIST_IN_DB_MESSAGE"), certificateTag.getName()));
                    throw new DuplicateException(ERROR_CODE_DUPLICATE + ERROR_CODE_TAG_NOT_VALID, errorMessage);
                }
                tagDAO.save(certificateTag);
                CertificateTag newTag = tagDAO.findByName(certificateTag.getName()).get();
                certificateDAO.saveIdsInHas_tagTable(giftCertificate.getId(), newTag.getId());
            }
        }
        if (giftCertificateFromDB.getTags().size() == 0) {
            if (certificateTagsToUpdate != null) {
                for (CertificateTag certificateTag : certificateTagsToUpdate
                ) {
                    certificateDAO.saveIdsInHas_tagTable(giftCertificate.getId(), certificateTag.getId());
                }
            }
        }

        for (CertificateTag certificateTag : giftCertificateFromDB.getTags()
        ) {
            if (!certificateTagsToUpdate.contains(certificateTag)) {
                certificateDAO.deleteIdsInHas_TagTable(giftCertificate.getId(), certificateTag.getId());
            }
        }
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
        for (CertificateTag certificateTag : giftCertificate.getTags()
        ) {
            certificateDAO.deleteIdsInHas_TagTable(giftCertificate.getId(), certificateTag.getId());
        }
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

//    /**
//     * Deletes all tags from the {@link GiftCertificate}.
//     *
//     * @param id is the id of the {@link GiftCertificate} which {@link CertificateTag}s to delete.
//     */
//    @Override
//    public void deleteAllTagsCertificate(long id) {
//        Optional<GiftCertificate> certificate = checkAndGetGiftCertificateById(id);
//
//        for (CertificateTag certificateTag : certificate.get().getTags()
//        ) {
//            certificateDAO.deleteIdsInHas_TagTable(id, certificateTag.getId());
//        }
//    }

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

//    /**
//     * Deletes {@link CertificateTag} from the {@link GiftCertificate}.
//     *
//     * @param certificateId is the id of the {@link GiftCertificate} which {@link CertificateTag} to delete.
//     * @param tagId         is the id of the {@link CertificateTag}, which is to delete.
//     */
//    @Override
//    public void deleteTagOfCertificate(long certificateId, long tagId) {
//        List<String> errorMessage = checkIfCertificateIdAndTagIdAreValid(certificateId, tagId);
//        Optional<GiftCertificate> certificate = certificateDAO.findById(certificateId);
//        Optional<CertificateTag> certificateTag = tagDAO.findById(tagId);
//        checkIfCertificateAndTagExistInSystem(errorMessage, certificate, certificateTag, certificateId, tagId);
//        if (!certificate.get().getTags().contains(certificateTag.get())) {
//            errorMessage.add(String.format(translator
//                    .toLocale("THE_CERTIFICATE_DOES_NOT_CONTAIN_THE_CERTIFICATE_TAG_WITH_SUCH_AN_ID"), tagId));
//            throw new MethodArgumentNotValidException(
//                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
//        }
//        certificateDAO.deleteIdsInHas_TagTable(certificateId, tagId);
//    }

//    private void checkIfCertificateAndTagExistInSystem(List<String> errorMessage,
//                                                       Optional<GiftCertificate> certificate,
//                                                       Optional<CertificateTag> certificateTag,
//                                                       long certificateId,
//                                                       long tagId) {
//        if (!certificate.isPresent()) {
//            errorMessage.add(String.format(translator.
//                    toLocale("THERE_IS_NO_A_CERTIFICATE_WITH_SUCH_AN_ID_IN_DATABASE"), certificateId));
//            if (!certificateTag.isPresent()) {
//                errorMessage.add(String.format(translator
//                        .toLocale("THERE_IS_NO_A_TAG_WITH_SUCH_AN_ID_IN_THE_SYSTEM"), tagId));
//            }
//            throw new EntityNotFoundException(
//                    ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
//        }
//    }

//    private List<String> checkIfCertificateIdAndTagIdAreValid(long certificateId, long tagId) {
//        List<String> errorMessage = new ArrayList<>();
//        if (certificateId < 0) {
//            errorMessage.add(translator.toLocale("THE_CERTIFICATE_ID_SHOULD_NOT_BE_LESS_THAN_0"));
//            if (tagId < 0) {
//                errorMessage.add(translator.toLocale("THE_TAG_ID_SHOULD_NOT_BE_LESS_THAN_0"));
//            }
//            throw new MethodArgumentNotValidException(
//                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
//        }
//        return errorMessage;
//    }

//    /**
//     * Adds the existed in the system {@link CertificateTag} to the {@link GiftCertificate}.
//     *
//     * @param certificateId is the id of the {@link GiftCertificate} to which {@link CertificateTag} is to add.
//     * @param tagId         is the id of the {@link CertificateTag}, which is to add.
//     */
//    @Override
//    public GiftCertificate addTagToCertificate(long certificateId, long tagId) {
//        List<String> errorMessage = checkIfCertificateIdAndTagIdAreValid(certificateId, tagId);
//        Optional<GiftCertificate> certificate = certificateDAO.findById(certificateId);
//        Optional<CertificateTag> certificateTag = tagDAO.findById(tagId);
//        checkIfCertificateAndTagExistInSystem(errorMessage, certificate, certificateTag, certificateId, tagId);
//        if (certificate.get().getTags().contains(certificateTag.get())) {
//            errorMessage.add(String.format(translator
//                    .toLocale("THE_CERTIFICATE_ALREADY_CONTAINS_THE_CERTIFICATE_TAG_WITH_SUCH_AN_ID"), tagId));
//            throw new MethodArgumentNotValidException(
//                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
//        }
//        certificateDAO.saveIdsInHas_tagTable(certificateId, tagId);
//        return certificateDAO.findById(certificateId).get();
//    }
//
//    /**
//     * Creates a new {@link CertificateTag} and adds it to the {@link GiftCertificate}.
//     *
//     * @param certificateId  is the id of the {@link GiftCertificate} to which {@link CertificateTag} is to add.
//     * @param certificateTag is the {@link CertificateTag}, which is to add.
//     */
//    @Override
//    public GiftCertificate addNewTagToCertificate(long certificateId, CertificateTag certificateTag) {
//        GiftCertificate giftCertificate = checkAndGetGiftCertificateById(certificateId).get();
//        tagValidator.validateTag(certificateTag, true);
//        tagDAO.save(certificateTag);
//        Optional<CertificateTag> newTagFromDB = tagDAO.findByName(certificateTag.getName());
//        certificateDAO.saveIdsInHas_tagTable(certificateId, newTagFromDB.get().getId());
//        return certificateDAO.findById(certificateId).get();
//    }

//    /**
//     * Updates the {@link GiftCertificate}.
//     *
//     * @param certificateName is the name of the {@link GiftCertificate}, which is to update.
//     * @param giftCertificate is the {@link GiftCertificate}, which contains the updated data.
//     */
//    @Override
//    public GiftCertificate updateCertificateByName(String certificateName, GiftCertificate giftCertificate) {
//        GiftCertificate giftCertificateFromDB = fetchAndCheckCertificateByName(certificateName).get();
//        fillValidateAndUpdateGiftCertificate(giftCertificate, giftCertificateFromDB);
//        return certificateDAO.findById(giftCertificateFromDB.getId()).get();
//    }

    private void fillValidateAndUpdateGiftCertificate(GiftCertificate giftCertificate, GiftCertificate giftCertificateFromDB) {
        fillCertificateValues(giftCertificate, giftCertificateFromDB);
        certificateValidator.validateCertificate(giftCertificate, false);
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        giftCertificate.setId(giftCertificateFromDB.getId());
        certificateDAO.update(giftCertificate);
        updateHasTagTable(giftCertificate, giftCertificateFromDB);
    }

//    /**
//     * Returns all {@link GiftCertificate}s which has the {@link CertificateTag} with the name equals {@param tagName}.
//     *
//     * @param tagName is the name of the {@link CertificateTag}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    @Override
//    public List<GiftCertificate> findCertificateByTagName(String tagName) {
//        Optional<CertificateTag> certificateTag = tagDAO.findByName(tagName);
//
//        if (!certificateTag.isPresent()) {
//            List<String> errorMessage = new ArrayList<>();
//            errorMessage.add(String.format(translator
//                    .toLocale("THERE_IS_NO_A_CERTIFICATE_TAG_WITH_SUCH_A_NAME_IN_DATABASE"), tagName));
//            throw new EntityNotFoundException(
//                    ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_TAG_NOT_VALID, errorMessage);
//        }
//        List<GiftCertificate> certificatesToReturn = new ArrayList<>();
//        List<GiftCertificate> allCertificates = certificateDAO.findAll();
//        for (GiftCertificate certificate : allCertificates
//        ) {
//            if (certificate.getTags().contains(certificateTag.get())) {
//                certificatesToReturn.add(certificate);
//            }
//        }
//        if (certificatesToReturn.isEmpty()) {
//            List<String> errorMessage = new ArrayList<>();
//            errorMessage.add(String.format(translator
//                    .toLocale("THERE_ARE_NO_CERTIFICATES_WHICH_CONTAINS_TAG_WITH_NAME"), tagName));
//            throw new EntityNotFoundException(
//                    ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
//        } else return certificatesToReturn;
//    }

//    /**
//     * Returns all {@link GiftCertificate}s which name contains {@param partSertName}.
//     *
//     * @param partSertName is the part of the name of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    @Override
//    public List<GiftCertificate> findCertificatesByPartSertName(String partSertName) {
//        List<GiftCertificate> allCertificates = certificateDAO.findAll();
//        return returnCertificatesByPartSertName(partSertName, allCertificates);
//    }

//    private List<GiftCertificate> returnCertificatesByPartSertName(String partSertName, List<GiftCertificate> allCertificates) {
//        List<GiftCertificate> certificatesToReturn = new ArrayList<>();
//        for (GiftCertificate certificate : allCertificates
//        ) {
//            if (certificate.getName().contains(partSertName)) {
//                certificatesToReturn.add(certificate);
//            }
//        }
//        if (certificatesToReturn.isEmpty()) {
//            List<String> errorMessage = new ArrayList<>();
//            errorMessage.add(String.format(translator
//                    .toLocale("THERE_ARE_NO_CERTIFICATES_WHICH_NAME_CONTAINS_PATH"), partSertName));
//            throw new EntityNotFoundException(
//                    ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
//        }
//        return certificatesToReturn;
//    }

//    /**
//     * Returns all {@link GiftCertificate}s which name contains {@param partDescrName}.
//     *
//     * @param partDescrName is the part of the description of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    @Override
//    public List<GiftCertificate> findCertificatesByPartDescrName(String partDescrName) {
//        List<GiftCertificate> allCertificates = certificateDAO.findAll();
//        return returnCertificatesByPartDescription(partDescrName, allCertificates);
//    }

//    private List<GiftCertificate> returnCertificatesByPartDescription(String partDescrName, List<GiftCertificate> allCertificates) {
//        List<GiftCertificate> certificatesToReturn = new ArrayList<>();
//        for (GiftCertificate certificate : allCertificates
//        ) {
//            if (certificate.getDescription().contains(partDescrName)) {
//                certificatesToReturn.add(certificate);
//            }
//        }
//        if (certificatesToReturn.isEmpty()) {
//            List<String> errorMessage = new ArrayList<>();
//            errorMessage.add(String.format(translator
//                    .toLocale("THERE_ARE_NO_CERTIFICATES_WHICH_DESCRIPTION_CONTAINS_PATH"), partDescrName));
//            throw new EntityNotFoundException(
//                    ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_CERTIFICATE_NOT_VALID, errorMessage);
//        }
//        return certificatesToReturn;
//    }

//    /**
//     * Returns all {@link GiftCertificate}s which which has the {@link CertificateTag}
//     * with the name equals {@param tagName} and which name contains {@param partSertName}.
//     *
//     * @param tagName      is the name of the {@link CertificateTag}, which is to find.
//     * @param partSertName is the part of the name of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    @Override
//    public List<GiftCertificate> findCertificatesByTagNameAndPartSertName(String tagName, String partSertName) {
//        List<GiftCertificate> afterFindByTagName = findCertificateByTagName(tagName);
//        return returnCertificatesByPartSertName(partSertName, afterFindByTagName);
//    }

//    /**
//     * Returns all {@link GiftCertificate}s which name contains {@param partSertName} and which description
//     * contains {@param partDescrName}.
//     *
//     * @param partSertName  is the part of the name of the {@link GiftCertificate}, which is to find.
//     * @param partDescrName is the part of the description of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    @Override
//    public List<GiftCertificate> findCertificatesByPartSertNameAndPartDescrName(String partSertName, String partDescrName) {
//        List<GiftCertificate> allCertificates = certificateDAO.findAll();
//        List<GiftCertificate> afterNameSelection = returnCertificatesByPartSertName(partSertName, allCertificates);
//        return returnCertificatesByPartDescription(partDescrName, afterNameSelection);
//    }

//    /**
//     * Returns all {@link GiftCertificate}s which has the {@link CertificateTag}
//     * with the name equals {@param tagName} and which description contains {@param partDescrName}.
//     *
//     * @param tagName       is the name of the {@link CertificateTag}, which is to find.
//     * @param partDescrName is the part of the description of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    @Override
//    public List<GiftCertificate> findCertificatesByTagNameAndPartDescrName(String tagName, String partDescrName) {
//        List<GiftCertificate> afterFindByTagName = findCertificateByTagName(tagName);
//        return returnCertificatesByPartDescription(partDescrName, afterFindByTagName);
//    }

//    /**
//     * Returns all {@link GiftCertificate}s which has the {@link CertificateTag}
//     * with the name equals {@param tagName}, which description contains {@param partDescrName}
//     * and which name contains {@param partSertName}.
//     *
//     * @param tagName       is the name of the {@link CertificateTag}, which is to find.
//     * @param partSertName  is the part of the name of the {@link GiftCertificate} which is to find.
//     * @param partDescrName is the part of the description of the {@link GiftCertificate}, which is to find.
//     * @return {@link List<GiftCertificate>}.
//     */
//    @Override
//    public List<GiftCertificate> findCertificatesByAllParameters(String tagName, String partSertName, String partDescrName) {
//        List<GiftCertificate> afterFindByTagName = findCertificateByTagName(tagName);
//        return returnCertificatesByPartSertName(partSertName,
//                returnCertificatesByPartDescription(partDescrName, afterFindByTagName));
//    }
}