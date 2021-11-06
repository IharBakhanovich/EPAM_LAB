package com.epam.esm.service.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.TagService;
import com.epam.esm.validator.TagValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TagServiceImpl implements TagService {
    public static final String ERROR_CODE_DUPLICATE = "409";
    public static final String ERROR_CODE_ENTITY_NOT_FOUND = "404";
    public static final String ERROR_CODE_METHOD_ARGUMENT_NOT_VALID = "400";
    public static final String ERROR_CODE_TAG_NOT_VALID = "02";
    private final TagDao tagDAO;
    private final TagValidator tagValidator;
    private final Translator translator;

    @Autowired
    public TagServiceImpl(TagDao tagDAO, TagValidator tagValidator, Translator translator) {
        this.tagDAO = tagDAO;
        this.tagValidator = tagValidator;
        this.translator = translator;
    }

    /**
     * Creates a new {@link CertificateTag} in the system.
     *
     * @param certificateTag is the {@link CertificateTag} to create.
     */
    @Override
    public CertificateTag createCertificateTag(CertificateTag certificateTag) {
        checkTagIfExistInSystem(certificateTag);
        tagValidator.validateTag(certificateTag, true);
        tagDAO.save(certificateTag);
        return findCertificateTagByName(certificateTag.getName());
    }

    private void checkTagIfExistInSystem(CertificateTag certificateTag) {
        List<String> errorMessage = new ArrayList<>();
        if (tagDAO.findByName(certificateTag.getName()).isPresent()) {
            errorMessage.add(String.format(translator
                    .toLocale("TAG_WITH_SUCH_NAME_EXIST_IN_DB_MESSAGE"), certificateTag.getName()));
        }
        if (tagDAO.findById(certificateTag.getId()).isPresent()) {
            errorMessage.add(String.format(translator
                    .toLocale("TAG_WITH_SUCH_ID_IS_ALREADY_EXIST_IN_THE_SYSTEM"), certificateTag.getId()));
        }
        if (!errorMessage.isEmpty()) {
            throw new DuplicateException(ERROR_CODE_DUPLICATE + ERROR_CODE_TAG_NOT_VALID, errorMessage);
        }
    }

    /**
     * Returns all {@link CertificateTag}s in the system.
     */
    @Override
    public List<CertificateTag> findAllCertificateTags(Map<String, String> parameters) {
        List<String> errorMessage = new ArrayList<>();
        int pageNumber = Integer.parseInt(parameters.get(ColumnNames.PAGE_NUMBER_PARAM_NAME));
        int amountEntitiesOnThePage = Integer.parseInt(parameters.get(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME));
        checkLimitAndOffset(errorMessage, pageNumber, amountEntitiesOnThePage);
        return tagDAO.findAllPagination(pageNumber, amountEntitiesOnThePage);
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
     * Returns a {@link CertificateTag} by its id.
     *
     * @param id is the id to find in the system.
     */
    @Override
    public CertificateTag findCertificateTagById(long id) {
        checkId(id);
        Optional<CertificateTag> certificateTag = tagDAO.findById(id);
        if (!certificateTag.isPresent()) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_A_CERTIFICATE_TAG_WITH_SUCH_AN_ID_IN_DATABASE"), id));
            throw new EntityNotFoundException(ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_TAG_NOT_VALID, errorMessage);
        } else {
            return certificateTag.get();
        }
    }

    /**
     * Updates a {@link CertificateTag}.
     *
     * @param tagId          is the id of the {@link CertificateTag}, which is to update.
     * @param certificateTag is the {@link CertificateTag} to update.
     */
    @Override
    public CertificateTag updateCertificateTag(long tagId, CertificateTag certificateTag) {
        checkId(tagId);
        checkByTagIfTagPresentInDB(tagDAO.findById(tagId), tagId);
        CertificateTag certificateTagFromDB = tagDAO.findById(tagId).get();
        if (certificateTag.getName() != null) {
            List<String> errorMessage = new ArrayList<>();
            if (tagDAO.findByName(certificateTag.getName()).isPresent()) {
                errorMessage.add(String.format(translator
                                .toLocale("CERTIFICATE_TAG_WITH_SUCH_NAME_EXIST_IN_DB_MESSAGE"),
                        certificateTag.getName()));
                throw new DuplicateException(ERROR_CODE_DUPLICATE + ERROR_CODE_TAG_NOT_VALID, errorMessage);
            }
        }
        fillCertificateTagValues(certificateTag, certificateTagFromDB, tagId);
        tagValidator.validateTag(certificateTag, false);
        tagDAO.update(certificateTag);
//        CertificateTag tag = tagDAO.findById(tagId).get();
//        return tag;
        return certificateTag;
    }

    private void checkId(long tagId) {
        if (tagId < 0) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(translator.toLocale("THE_ID_SHOULD_NOT_BE_LESS_THAN_0"));
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_TAG_NOT_VALID, errorMessage);
        }
    }

    private void fillCertificateTagValues(CertificateTag certificateTag, CertificateTag certificateTagFromDB, long tagId) {
        certificateTag.setId(tagId);
        if (certificateTag.getName() == null) {
            certificateTag.setName(certificateTagFromDB.getName());
        }
    }

    /**
     * Deletes a {@link CertificateTag} by its id.
     *
     * @param id is the id of the {@link CertificateTag} to delete.
     */
    @Override
    public void deleteCertificateTag(long id) {
        checkId(id);
        Optional<CertificateTag> certificateTag = tagDAO.findById(id);
        checkByTagIfTagPresentInDB(certificateTag, id);
        tagDAO.delete(id);
        tagDAO.deleteFromHasTagByTagId(id);
    }

    private void checkByTagIfTagPresentInDB(Optional<CertificateTag> certificateTag, long id) {
        if (!certificateTag.isPresent()) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_A_CERTIFICATE_TAG_WITH_SUCH_AN_ID_IN_DATABASE"), id));
            throw new EntityNotFoundException(ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_TAG_NOT_VALID, errorMessage);
        }
    }

    /**
     * Returns a certificate by its name.
     *
     * @param name is the name of the {@link GiftCertificate} to find in the system.
     */
    @Override
    public CertificateTag findCertificateTagByName(String name) {
        List<String> errorMessage = new ArrayList<>();
        Optional<CertificateTag> certificateTag = tagDAO.findByName(name);
        if (!certificateTag.isPresent()) {
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_A_CERTIFICATE_TAG_WITH_SUCH_A_NAME_IN_DATABASE"), name));
            throw new EntityNotFoundException(ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_TAG_NOT_VALID, errorMessage);
        } else {
            return certificateTag.get();
        }
    }

    /**
     * Returns the most popular {@link CertificateTag} of the {@link User}
     * with the biggest sum of order price.
     *
     * @return {@link CertificateTag}
     */
    @Override
    public CertificateTag mostPopularTagOfTheBestUser() {
        return tagDAO.findTheMostPopularTagOfTheBestUser().orElse(null);
    }
}
