package com.epam.esm.service.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.OrderService;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.OrderValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
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
    private final OrderDao orderDao;
    private final CertificateValidator certificateValidator;
    private final TagValidator tagValidator;
    private final UserValidator userValidator;
    private final Translator translator;
    private final OrderValidator orderValidator;

    @Autowired

    public OrderServiceImpl(UserDao userDao, CertificateDao certificateDAO, TagDao tagDAO, OrderDao orderDao,
                            CertificateValidator certificateValidator, TagValidator tagValidator,
                            UserValidator userValidator, Translator translator, OrderValidator orderValidator) {
        this.userDao = userDao;
        this.certificateDAO = certificateDAO;
        this.tagDAO = tagDAO;
        this.orderDao = orderDao;
        this.certificateValidator = certificateValidator;
        this.tagValidator = tagValidator;
        this.userValidator = userValidator;
        this.translator = translator;
        this.orderValidator = orderValidator;
    }

    /**
     * Creates a new {@link Order} in the system.
     *
     * @param order is the {@link Order} to create.
     * @return created {@link Order}
     */
    @Override
    public Order createOrder(Order order) {
        return null;
    }

    private void checkForErrors(List<String> errorMessage, String errorMessageAfterCheckUserId, String errorMessageAfterCheckCertificateId) {
        if (errorMessageAfterCheckUserId != null) {
            errorMessage.add(errorMessageAfterCheckUserId);
        }
        if (errorMessageAfterCheckCertificateId != null) {
            errorMessage.add(errorMessageAfterCheckCertificateId);
        }
        if (!errorMessage.isEmpty()) {
            throw new MethodArgumentNotValidException(
                    ERROR_CODE_METHOD_ARGUMENT_NOT_VALID + ERROR_CODE_ORDER_NOT_VALID, errorMessage);
        }
    }

    /**
     * Orders a {@link GiftCertificate} to the {@link User}
     *
     * @param userId        is the id of the {@link User} that order a {@param giftCertificate}.
     * @param certificateId is the id of the {@link GiftCertificate} that is ordered by {@param user}.
     * @return {@link Order}.
     */
    @Override
    @SneakyThrows
    public Order orderCertificate(long userId, long certificateId) {
        // checks inputs
        List<String> errorMessage = new ArrayList<>();
        String errorMessageAfterCheckUserId = checkId(userId, "userId");
        String errorMessageAfterCheckCertificateId = checkId(certificateId, "certificateId");
        checkForErrors(errorMessage, errorMessageAfterCheckUserId, errorMessageAfterCheckCertificateId);
        Optional<User> user = userDao.findById(userId);
        Optional<GiftCertificate> giftCertificate = certificateDAO.findById(certificateId);
        String errorMessageAfterCheckUser = checkUserExistence(user, userId);
        String errorMessageAfterCheckCertificate = checkCertificateExistence(giftCertificate, certificateId);
        checkForErrors(errorMessage, errorMessageAfterCheckCertificate, errorMessageAfterCheckUser);
        // creates new order
        List<GiftCertificate> certificates = new ArrayList<>();
        certificates.add(giftCertificate.get());
        String name = generateUniqueOrderName();
        Order newOrder = new Order(0, user.get(), LocalDateTime.now(), name, certificates);
        orderDao.save(newOrder);
        Order orderFromDB = orderDao.findByName(name).get();
        orderDao.saveIdsInUserorder_certificateTable(orderFromDB.getId(),
                giftCertificate.get().getId(), giftCertificate.get());
        return orderDao.findByName(name).get();
    }

    private String checkCertificateExistence(Optional<GiftCertificate> giftCertificate, long id) {
        if (!giftCertificate.isPresent()) {
            return String.format(translator.toLocale("THERE_IS_NO_A_CERTIFICATE_WITH_SUCH_AN_ID_IN_DATABASE"), id);
        }
        return null;
    }

    private String checkUserExistence(Optional<User> user, long id) {
        if (!user.isPresent()) {
            return String.format(translator.toLocale("THERE_IS_NO_A_USER_WITH_SUCH_AN_ID_IN_DATABASE"), id);
        }
        return null;
    }

    private String checkId(long id, String label) {
        if (id < 0) {
            return String.format(translator.toLocale("SOME_ID_SHOULD_NOT_BE_LESS_THAN_ONE"), label);
        }
        return null;
    }

    /**
     * Generates a unique name of a new order.
     *
     * @return generated name, that is unique in the system.
     */
    @Override
    @SneakyThrows
    public String generateUniqueOrderName() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString("Order_" + LocalDateTime.now());
    }

    /**
     * Returns a {@link Order} by its id.
     *
     * @param id is the id to find in the system.
     */
    @Override
    public Order findOrderById(long id) {
        checkId(id, "orderId");
        Optional<Order> order = orderDao.findById(id);
        return getOrderIfPresent(id, order);
    }

    private Order getOrderIfPresent(long id, Optional<Order> order) {
        if (!order.isPresent()) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(String.format(translator
                    .toLocale("THERE_IS_NO_A_ORDER_WITH_SUCH_AN_ID_IN_DATABASE"), id));
            throw new EntityNotFoundException(ERROR_CODE_ENTITY_NOT_FOUND + ERROR_CODE_ORDER_NOT_VALID, errorMessage);
        } else {
            return order.get();
        }
    }

    /**
     * Returns all {@link Order}s in the system.
     *
     * @param parameters is all the query parameters in the URI.
     * @return {@link List<Order>}, that represents all the orders in the system.
     */
    @Override
    public List<Order> findAllOrders(Map<String, String> parameters) {
        return orderDao.findAll();
    }
}