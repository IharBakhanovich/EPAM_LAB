package com.epam.esm.service.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final OrderService orderService;
    private final OrderDao orderDao;
    private final ConversionService conversionService;

    @Autowired
    public UserServiceImpl(UserDao userDao, CertificateDao certificateDAO, TagDao tagDAO,
                           CertificateValidator certificateValidator, TagValidator tagValidator,
                           UserValidator userValidator, Translator translator,
                           OrderService orderService, OrderDao orderDao, ConversionService conversionService) {
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
        List<String> errorMessage = new ArrayList<>();
        int pageNumber = Integer.parseInt(parameters.get(ColumnNames.PAGE_NUMBER_PARAM_NAME));
        int amountEntitiesOnThePage = Integer.parseInt(parameters.get(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME));
        checkLimitAndOffset(errorMessage, pageNumber, amountEntitiesOnThePage);
        List<User> users = userDao.findAllPagination(pageNumber, amountEntitiesOnThePage);
        return users;
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
    public User createUser(User user) {
        List<String> errorMessage = new ArrayList<>();
        if (user.getNickName() != null) {
            checkIfUserExistInSystem(user, errorMessage);
            if (user.getOrders() == null || user.getOrders().isEmpty()) {
                return createNewUser(user);
            } else {
                for (Order order : user.getOrders()) {
                    for (GiftCertificate certificate : order.getCertificates()) {
                        checkCertificateId(errorMessage, certificate);
                    }
                }
                if (!errorMessage.isEmpty()) {
                    throw new EntityNotFoundException(
                            ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_USER_NOT_VALID, errorMessage);
                }
                User createdUser = createNewUser(user);
                for (Order order : user.getOrders()) {
                    addOrdersToUser(user, createdUser, order);
                }
            }
        } else {
            errorMessage.add(translator.toLocale("THE_FIELD_NICKNAME_SHOULD_BE_NOT_EMPTY"));
            throw new MethodArgumentNotValidException(ERROR_CODE_DUPLICATE + ERROR_CODE_USER_NOT_VALID, errorMessage);
        }
        return userDao.findByName(user.getNickName()).get();
    }

    private void addOrdersToUser(User user, User createdUser, Order order) {
        String name = orderService.generateUniqueOrderName(user);
        Order newOrder = new Order(0, createdUser, LocalDateTime.now(), name, new ArrayList<>());
        orderDao.save(newOrder);
        Order orderFromDB = orderDao.findByName(name).get();
        for (GiftCertificate certificate : order.getCertificates()) {
            GiftCertificate certificateToAddToOrder = certificateDAO.findById(certificate.getId()).get();
            orderDao.saveIdsInUserorder_certificateTable(
                    orderFromDB.getId(), certificate.getId(),
                    certificateToAddToOrder, certificateToAddToOrder.getPrice());
        }
    }

    private User createNewUser(User user) {
        userValidator.validateUser(user, true);
        userDao.save(user);
        Optional<User> userFromDB = userDao.findByName(user.getNickName());
        return userFromDB.get();
    }

    private void checkCertificateId(List<String> errorMessage, GiftCertificate certificate) {
        if (certificate.getId() == 0) {
            errorMessage.add(translator.toLocale("CERTIFICATE_ID_SHOULD_NOT_BE_EMPTY"));
        } else if (certificate.getId() < 0) {
            errorMessage.add(String.format(
                    translator.toLocale("SOME_ID_SHOULD_NOT_BE_LESS_THAN_ONE"), "certificateId"));
        } else if (!certificateDAO.findById(certificate.getId()).isPresent()) {
            errorMessage.add(String.format(translator.toLocale(
                    "THERE_IS_NO_A_CERTIFICATE_WITH_SUCH_AN_ID_IN_DATABASE"), certificate.getId()));
        }
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
    public User fetchUserById(long userId) {
        checkId(userId);
        Optional<User> user = userDao.findById(userId);
        if (!user.isPresent()) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_A_USER_WITH_SUCH_AN_ID_IN_DATABASE"), userId));
            throw new EntityNotFoundException(ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_USER_NOT_VALID, errorMessage);
        } else {
            return user.get();
        }
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
        Optional<User> userFromDB = userDao.findById(userId);
        Optional<Order> order = getUserIfPresent(userId, userFromDB).getOrders()
                .stream()
                .filter(order1 -> order1.getId()==orderId)
                .findFirst();
        if (!order.isPresent()) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_AN_ORDER_WITH_SUCH_AN_ID_BY_THE_USER_WITH_ID"), orderId, userId));
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_USER_NOT_VALID, errorMessage);
        } else {
            OrderDto orderDto = conversionService.convert(order.get(), OrderDto.class);
            return orderDto;
        }
    }
}
