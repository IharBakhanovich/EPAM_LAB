package com.epam.esm.controller;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * API to work with {@link User}s of the GiftCertificatesShop.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final Translator translator;

    /**
     * Constructs the {@link UserController}.
     *
     * @param userService is the service to inject.
     */
    @Autowired
    public UserController(UserService userService, Translator translator) {
        this.userService = userService;
        this.translator = translator;
    }

    /**
     * The method that realises the 'GET /users/{userId}/orders' query.
     *
     * @param userId is the ID of the {@link User} to find.
     * @return {@link List<Order>} that belong ti the {@link User} with the id equals userId.
     */
    @GetMapping(value = "/{userId}/orders")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<EntityModel<Order>> userOrders(@PathVariable("userId") long userId) {
        List<Order> orders = userService.findUserById(userId).getOrders();
        List<EntityModel<Order>> modelFromOrders = new ArrayList<>();
        if (!orders.isEmpty()) {
            modelFromOrders = orders.stream().map(order -> EntityModel.of(order,
                            linkTo(methodOn(OrderController.class).fetchOrderById(order.getId())).withSelfRel(),
                            linkTo(methodOn(UserController.class).getUserById(userId))
                                    .withRel(translator.toLocale("FETCHES_USER_HATEOAS_LINK_MESSAGE")),
                            linkTo(methodOn(UserController.class).addNewUser(new User()))
                                    .withRel(translator.toLocale("CREATES_NEW_USER_HATEOAS_LINK_MESSAGE"))))
                    .collect(Collectors.toList());
//            linkTo(methodOn(OrderController.class).orderCertificate(order.getUser().getId(), order.getCertificates().get(0).getId()))
//                    .withRel(translator.toLocale("USER_ORDERS_CERTIFICATE_HATEOAS_LINK_MESSAGE"))
        }
        return CollectionModel.of(modelFromOrders, linkTo(methodOn(UserController.class)
                        .userOrders(userId)).withSelfRel(),
                linkTo(methodOn(UserController.class).fetchAllUsers(ColumnNames.DEFAULT_PARAMS))
                        .withRel(translator.toLocale("FETCHES_ALL_USERS_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(OrderController.class).fetchAllOrders(ColumnNames.DEFAULT_PARAMS))
                        .withRel(translator.toLocale("FETCHES_ALL_ORDERS_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(GiftCertificateController.class).certificates(ColumnNames.DEFAULT_PARAMS))
                        .withRel(translator.toLocale("FETCHES_ALL_CERTIFICATES_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(CertificateTagController.class).tags(ColumnNames.DEFAULT_PARAMS))
                        .withRel(translator.toLocale("FETCHES_ALL_TAGS_HATEOAS_LINK_MESSAGE")));
    }

    /**
     * The method that realises the 'POST /users' query.
     *
     * @param user is the {@link User} to create.
     * @return the created {@link User}.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addNewUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * The method that realises the 'GET /users/{userId}' query.
     *
     * @param userId is the ID of the {@link CertificateTag} to find.
     * @return {@link CertificateTag} with the tagId if such an id exists in the system.
     */
    @GetMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<User> getUserById(@PathVariable("userId") long userId) {
        User user = userService.fetchUserById(userId);
        EntityModel<User> userEntityModel = EntityModel.of(user, linkTo(methodOn(UserController.class)
                .userOrders(userId))
                .withRel(translator.toLocale("ALL_ORDERS_OF_THIS_USER_HATEOAS_LINK_MESSAGE")));
        if (!user.getOrders().isEmpty()) {
            userEntityModel.add(linkTo(methodOn(UserController.class)
                    .costAndTimeOfUsersOrder(user.getId(), user.getOrders().get(0).getId()))
                    .withRel(translator.toLocale("COST_AND_TIME_OF_THE_USER_ORDER_HATEOAS_LINK_MESSAGE")));
//            userEntityModel.add(linkTo(methodOn(OrderController.class)
//                    .orderCertificate(userId, user.getOrders().get(0).getCertificates().get(0).getId()))
//                    .withRel(translator.toLocale("USER_ORDERS_CERTIFICATE_HATEOAS_LINK_MESSAGE")));
        }
        userEntityModel.add(linkTo(methodOn(UserController.class).fetchAllUsers(new HashMap<>()))
                .withRel(translator.toLocale("FETCHES_ALL_USERS_HATEOAS_LINK_MESSAGE")));
        userEntityModel.add(linkTo(methodOn(UserController.class).getUserById(userId)).withSelfRel());
        return userEntityModel;
    }

    /**
     * The method that realises the 'GET /users' query.
     *
     * @param parameters: there are following parameters, which can be applied to the query:
     *                    -  offset=0/MAX_VALUE is the long to pass records from database.
     *                    To fetch records from 6 record 'offset' should be set to 5.
     *                    - limit = 0/MAX_VALUE is the long to set how many records should be fetched.
     * @return {@link List<User>} - all {@link User}s in the system.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<EntityModel<User>> fetchAllUsers(@RequestParam Map<String, String> parameters) {
        parameters = ColumnNames.validateParameters(parameters, ColumnNames.DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
        List<User> users = userService.findAllUsers(parameters);
        int pageNumber = Integer.parseInt(parameters.get(ColumnNames.PAGE_NUMBER_PARAM_NAME));
        int amountEntitiesOnThePage
                = Integer.parseInt(parameters.get(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME));
        Map<String, String> paramsNext = ColumnNames.createNextParameters(users, pageNumber, amountEntitiesOnThePage);
        Map<String, String> paramsPrev = ColumnNames.createPrevParameters(users, pageNumber, amountEntitiesOnThePage);
        List<EntityModel<User>> modelFromOrders = users.stream().map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId()))
                                .withRel(translator.toLocale("FETCHES_THE_USER_HATEOAS_LINK_MESSAGE"))))
                .collect(Collectors.toList());
        return CollectionModel.of(modelFromOrders,
                linkTo(methodOn(GiftCertificateController.class).certificates(ColumnNames.DEFAULT_PARAMS))
                        .withRel(translator.toLocale("FETCHES_ALL_CERTIFICATES_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(CertificateTagController.class).tags(ColumnNames.DEFAULT_PARAMS))
                        .withRel(translator.toLocale("FETCHES_ALL_TAGS_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(OrderController.class).fetchAllOrders(ColumnNames.DEFAULT_PARAMS))
                        .withRel(translator.toLocale("FETCHES_ALL_ORDERS_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(UserController.class).fetchAllUsers(paramsPrev))
                        .withRel(translator.toLocale("FETCHES_PREVIOUS_PAGE_USERS_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(UserController.class).fetchAllUsers(paramsNext))
                        .withRel(translator.toLocale("FETCHES_NEXT_PAGE_USERS_HATEOAS_LINK_MESSAGE")),
                linkTo(methodOn(UserController.class).fetchAllUsers(parameters)).withSelfRel());
    }

    /**
     * The method that realises the 'GET /users/{userId}/orders/{orderId}/costAndTime' query.
     *
     * @param userId is the ID of the {@link User} to find.
     * @return {@link List<Order>} that belong ti the {@link User} with the id equals userId.
     */
    @GetMapping(value = "/{userId}/orders/{orderId}/costAndTime")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<OrderDto> costAndTimeOfUsersOrder(@PathVariable("userId") long userId,
                                                         @PathVariable("orderId") long orderId) {
        OrderDto orderDto = userService.findUserOrderByOrderIdCostAndTime(userId, orderId);
        EntityModel<OrderDto> orderDtoEntityModel = EntityModel.of(orderDto, linkTo(methodOn(OrderController.class)
                .fetchOrderById(orderId)).withRel(translator
                .toLocale("FETCHES_ORDER_HATEOAS_LINK_MESSAGE")));
        orderDtoEntityModel.add(linkTo(methodOn(UserController.class).getUserById(userId))
                .withRel(translator.toLocale("FETCHES_USER_HATEOAS_LINK_MESSAGE")));
        orderDtoEntityModel.add(linkTo(methodOn(UserController.class).userOrders(userId))
                .withRel(translator.toLocale("FETCHES_ALL_ORDERS_HATEOAS_LINK_MESSAGE")));

        return orderDtoEntityModel.add(linkTo(methodOn(UserController.class).costAndTimeOfUsersOrder(userId, orderId))
                .withSelfRel());
    }
}
