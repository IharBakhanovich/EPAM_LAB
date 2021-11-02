package com.epam.esm.controller;

import com.epam.esm.dao.impl.ColumnNames;
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

    /**
     * Constructs the {@link UserController}.
     *
     * @param userService is the service to inject.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        List<EntityModel<Order>> modelFromOrders = orders.stream().map(order -> EntityModel.of(order,
                        linkTo(methodOn(OrderController.class).fetchOrderById(order.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUserById(userId))
                                .withRel("Fetches this user: GET"),
                        linkTo(methodOn(UserController.class).addNewUser(new User()))
                                .withRel("Adds new user in the system: POST"),
                linkTo(methodOn(OrderController.class)
                        .orderCertificate(order.getUser().getId(), order.getCertificates().get(0).getId()))
                        .withRel("User can order a certificate (params: userId/certificateId): POST")))
                .collect(Collectors.toList());
        return CollectionModel.of(modelFromOrders, linkTo(methodOn(UserController.class)
                        .userOrders(userId)).withSelfRel(),
                linkTo(methodOn(UserController.class).fetchAllUsers(ColumnNames.DEFAULT_PARAMS))
                        .withRel("Fetches all users: GET"),
                linkTo(methodOn(OrderController.class).fetchAllOrders(ColumnNames.DEFAULT_PARAMS))
                        .withRel("Fetches all orders: GET"),
                linkTo(methodOn(GiftCertificateController.class).certificates(ColumnNames.DEFAULT_PARAMS))
                        .withRel("Fetches all certificates: GET"),
                linkTo(methodOn(CertificateTagController.class).tags(ColumnNames.DEFAULT_PARAMS))
                        .withRel("Fetches all tags: GET"));
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
                .userOrders(userId)).withRel("All certificates this user: GET"));
        userEntityModel.add(linkTo(methodOn(UserController.class)
                .costAndTimeOfUsersOrder(user.getId(), user.getOrders().get(0).getId()))
                .withRel("Cost and ordered time of the first user order (inputs: userId, orderId): GET"));
        userEntityModel.add(linkTo(methodOn(UserController.class).fetchAllUsers(new HashMap<>()))
                .withRel("Fetches all users in the system: GET"));
        userEntityModel.add(linkTo(methodOn(OrderController.class)
                .orderCertificate(userId, user.getOrders().get(0).getCertificates().get(0).getId()))
                .withRel("User can order a certificate (params: userId/certificateId): POST"));
        userEntityModel.add(linkTo(methodOn(UserController.class).getUserById(userId)).withSelfRel());
        return  userEntityModel;
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
        parameters = ColumnNames.validateParameters(parameters, ColumnNames.DEFAULT_ENTITIES_ON_THE_PAGE);
        List<User> users = userService.findAllUsers(parameters);
        long offset = Long.parseLong(parameters.get(ColumnNames.OFFSET_PARAM_NAME));
        long limit = Long.parseLong(parameters.get(ColumnNames.LIMIT_PARAM_NAME));
        Map<String, String> paramsNext = ColumnNames.createNextParameters(users, offset, limit);
        Map<String,String> paramsPrev = ColumnNames.createPrevParameters(users, offset, limit);
        List<EntityModel<User>> moderFromOrders = users.stream().map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId()))
                                .withRel("Fetches and removes user from the system (params: userId): GET, DELETE")))
                .collect(Collectors.toList());
        return CollectionModel.of(moderFromOrders,
                linkTo(methodOn(GiftCertificateController.class).certificates(ColumnNames.DEFAULT_PARAMS))
                        .withRel("Fetches all certificates: GET"),
                linkTo(methodOn(CertificateTagController.class).tags(ColumnNames.DEFAULT_PARAMS))
                        .withRel("Fetches all tags: GET"),
                linkTo(methodOn(OrderController.class).fetchAllOrders(ColumnNames.DEFAULT_PARAMS))
                        .withRel("Fetches all orders: GET"),
                linkTo(methodOn(UserController.class).fetchAllUsers(paramsPrev))
                        .withRel("Fetches PREVIOUS PAGE of users: GET"),
                linkTo(methodOn(UserController.class).fetchAllUsers(paramsNext))
                        .withRel("Fetches NEXT PAGE of users: GET"),
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
                        .fetchOrderById(orderId)).withRel("Fetches all details of this order(inputs: orderId): GET"));
        orderDtoEntityModel.add(linkTo(methodOn(UserController.class).getUserById(userId))
                .withRel("Fetches this user (inputs: userId): GET"));
        orderDtoEntityModel.add(linkTo(methodOn(UserController.class).userOrders(userId))
                .withRel("Fetches all user's orders: GET"));

        return orderDtoEntityModel.add(linkTo(methodOn(UserController.class).costAndTimeOfUsersOrder(userId, orderId))
                .withSelfRel());
    }
}
