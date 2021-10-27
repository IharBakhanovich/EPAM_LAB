package com.epam.esm.service.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.UserService;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    public static final String ERROR_CODE_DUPLICATE = "409";
    public static final String ERROR_CODE_ENTITY_NOT_FOUND = "404";
    public static final String ERROR_CODE_METHOD_ARGUMENT_NOT_VALID = "400";
    public static final String ERROR_CODE_CERTIFICATE_NOT_VALID = "01";
    public static final String ERROR_CODE_TAG_NOT_VALID = "02";
    public static final String ERROR_CODE_USER_NOT_VALID = "04";

    private final UserDao userDao;
    private final CertificateDao certificateDAO;
    private final TagDao tagDAO;
    private final CertificateValidator certificateValidator;
    private final TagValidator tagValidator;
    private final UserValidator userValidator;
    private final Translator translator;

    @Autowired
    public UserServiceImpl(UserDao userDao, CertificateDao certificateDAO, TagDao tagDAO,
                           CertificateValidator certificateValidator, TagValidator tagValidator,
                           UserValidator userValidator, Translator translator) {
        this.userDao = userDao;
        this.certificateDAO = certificateDAO;
        this.tagDAO = tagDAO;
        this.certificateValidator = certificateValidator;
        this.tagValidator = tagValidator;
        this.userValidator = userValidator;
        this.translator = translator;
    }

    /**
     * Returns a {@link User} by its id.
     *
     * @param id is the id of the {@link User} to find in the system.
     */
    @Override
    public User findUserById(long id) {
        checkId(id);
        Optional<User> user = userDao.findById(id);
        return getUserIfPresent(id, user);
    }

    private User getUserIfPresent(long id, Optional<User> user) {
        if (!user.isPresent()) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_A_USER_WITH_SUCH_AN_ID_IN_DATABASE"), id));
            throw new EntityNotFoundException(ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_USER_NOT_VALID, errorMessage);
        } else {
            return user.get();
        }
    }

    private void checkId(long tagId) {
        if (tagId < 0) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(translator.toLocale("THE_ID_SHOULD_NOT_BE_LESS_THAN_0"));
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_USER_NOT_VALID, errorMessage);
        }
    }

    /**
     * Returns all {@link User}s in the system.
     *
     * @param parameters is all the query parameters in the URI.
     * @return {@link List<User>}, that represents all the users in the system.
     */
    @Override
    public List<User> findAllUsers(Map<String, String> parameters) {
        return userDao.findAll();
    }
}
