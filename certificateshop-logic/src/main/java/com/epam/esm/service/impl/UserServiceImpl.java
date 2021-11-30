package com.epam.esm.service.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.converter.UserToUserDtoConverter;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.Role;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    public static final String ERROR_CODE_DUPLICATE = "409";
    public static final String ERROR_CODE_ENTITY_NOT_FOUND = "404";
    public static final String ERROR_CODE_METHOD_ARGUMENT_NOT_VALID = "400";
    public static final String ERROR_CODE_CERTIFICATE_NOT_VALID = "01";
    public static final String ERROR_CODE_TAG_NOT_VALID = "02";
    public static final String ERROR_CODE_USER_NOT_VALID = "04";
    public static final String ERROR_CODE_ORDER_NOT_VALID = "03";

    private final UserDao userDao;
    private final CertificateDao certificateDAO;
    private final TagDao tagDAO;
    private final CertificateValidator certificateValidator;
    private final TagValidator tagValidator;
    private final UserValidator userValidator;
    private final Translator translator;
    private final OrderService orderService;
    private final OrderDao orderDao;
    private final ConversionService conversionService;
    private final PasswordEncoder passwordEncoder;
    private final UserToUserDtoConverter userToUserDtoConverter;

    @Autowired
    public UserServiceImpl(UserDao userDao, CertificateDao certificateDAO, TagDao tagDAO,
                           CertificateValidator certificateValidator, TagValidator tagValidator,
                           UserValidator userValidator, Translator translator, OrderService orderService,
                           OrderDao orderDao, ConversionService conversionService, PasswordEncoder passwordEncoder,
                           UserToUserDtoConverter userToUserDtoConverter) {
        this.userDao = userDao;
        this.certificateDAO = certificateDAO;
        this.tagDAO = tagDAO;
        this.certificateValidator = certificateValidator;
        this.tagValidator = tagValidator;
        this.userValidator = userValidator;
        this.translator = translator;
        this.orderService = orderService;
        this.orderDao = orderDao;
        this.conversionService = conversionService;
        this.passwordEncoder = passwordEncoder;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    /**
     * Returns a {@link User} by its id.
     *
     * @param id is the id of the {@link User} to find in the system.
     */
    @Override
    public UserDto findUserById(long id) {
        checkId(id);
        Optional<User> user = userDao.findById(id);
        return conversionService.convert(getUserIfPresent(id, user), UserDto.class);
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
    public List<UserDto> findAllUsers(Map<String, String> parameters) {
        List<String> errorMessage = new ArrayList<>();
        int pageNumber = Integer.parseInt(parameters.get(ColumnNames.PAGE_NUMBER_PARAM_NAME));
        int amountEntitiesOnThePage = Integer.parseInt(parameters.get(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME));
        checkLimitAndOffset(errorMessage, pageNumber, amountEntitiesOnThePage);
        List<User> users = userDao.findAllPagination(pageNumber, amountEntitiesOnThePage);
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(conversionService.convert(user, UserDto.class));
        }
        return userDtos;
    }

    private void checkLimitAndOffset(List<String> errorMessage, long offset, long limit) {
        if (offset < 0) {
            errorMessage.add(translator.toLocale("THE_OFFSET_SHOULD_BE_MORE_THAN_0"));
        }
        if (limit < 0) {
            errorMessage.add(translator.toLocale("THE_LIMIT_SHOULD_BE_MORE_THAN_0"));
        }
        if (!errorMessage.isEmpty()) {
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_USER_NOT_VALID, errorMessage);
        }
    }

    /**
     * Creates a new {@link User} in the system.
     *
     * @param user is the {@link User} to create.
     */
    @Override
    public UserDto createUser(User user) {
        List<String> errorMessage = new ArrayList<>();
        if (user.getNickName() != null && !user.getNickName().trim().equals("")) {
            checkIfUserExistInSystem(user, errorMessage);
            return createNewUser(user);
        } else {
            errorMessage.add(translator.toLocale("THE_FIELD_NICKNAME_SHOULD_BE_NOT_EMPTY"));
            throw new MethodArgumentNotValidException(ERROR_CODE_DUPLICATE + ERROR_CODE_USER_NOT_VALID, errorMessage);
        }
    }

    private UserDto createNewUser(User user) {
        userValidator.validateUser(user, true);
        // always created new user with the Role.USER
        User userToSave = new User(0, user.getNickName(), passwordEncoder.encode(user.getPassword()), Role.USER);
        userDao.save(userToSave);
        Optional<User> userFromDB = userDao.findByName(userToSave.getNickName());
        return conversionService.convert(userFromDB.get(), UserDto.class);
    }

    private void checkIfUserExistInSystem(User user, List<String> errorMessage) {
        if (userDao.findByName(user.getNickName()).isPresent()) {
            errorMessage.add(String.format(translator
                    .toLocale("SUCH_A_USER_IS_ALREADY_EXIST_IN_THE_SYSTEM"), user.getNickName()));
            throw new DuplicateException(ERROR_CODE_DUPLICATE + ERROR_CODE_USER_NOT_VALID, errorMessage);
        }
    }

    /**
     * Returns a {@link User} by its id.
     *
     * @param userId is the id to find in the system.
     */
    @Override
    public UserDto fetchUserById(long userId) {
        checkId(userId);
        Optional<User> user = userDao.findById(userId);
        if (!user.isPresent()) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_A_USER_WITH_SUCH_AN_ID_IN_DATABASE"), userId));
            throw new EntityNotFoundException(ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_USER_NOT_VALID, errorMessage);
        }
        return conversionService.convert(user.get(), UserDto.class);
    }

    /**
     * Return the price and the timestamp of a purchase of the user's {@link Order} with the {@param orderId},
     * that was purchased by {@link User} with the {@param userId}.
     *
     * @param userId  is the {@link User} ID to find by.
     * @param orderId is the {@link Order} ID to find by.
     * @return {@link OrderDto}
     */
    @Override
    public OrderDto findUserOrderByOrderIdCostAndTime(long userId, long orderId) {
        checkId(userId);
        checkId(orderId);
        List<Order> orders = orderDao.findAllByUserId(userId);
        Optional<User> userFromDB = userDao.findById(userId);

        Optional<Order> order = orders
                .stream()
                .filter(order1 -> order1.getId() == orderId)
                .findFirst();

        if (!order.isPresent()) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_AN_ORDER_WITH_SUCH_AN_ID_BY_THE_USER_WITH_ID"), orderId, userId));
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_USER_NOT_VALID, errorMessage);
        } else {
            return conversionService.convert(order.get(), OrderDto.class);
        }
    }
}
