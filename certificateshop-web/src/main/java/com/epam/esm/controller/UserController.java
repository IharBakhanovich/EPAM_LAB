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
        Map<String, String> parameters = new HashMap<>();
        parameters.put("offset", "0");
        parameters.put("limit", ColumnNames.DEFAULT_ENTITIES_ON_THE_PAGE);
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
        return CollectionModel.of(modelFromOrders, linkTo(methodOn(UserController.class).userOrders(userId)).withSelfRel(),
                linkTo(methodOn(UserController.class).fetchAllUsers(new HashMap<String, String>()))
                        .withRel("Fetches all users: GET"),
                linkTo(methodOn(OrderController.class).fetchAllOrders()).withRel("Fetches all orders: GET"),
                linkTo(methodOn(GiftCertificateController.class).certificates(new HashMap<>()))
                        .withRel("Fetches all certificates: GET"),
                linkTo(methodOn(CertificateTagController.class).tags(parameters)).withRel("Fetches all tags: GET"));
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
     *                    - tag_name=123 is the name of the tag by which the query will be executed and only certificates,
     *                    that contain the tag with a mentioned name will be shown;
     *                    - part_cert_name=123 is the part of a certificate name. Only certificates, which contain
     *                    the value in their names will be shown;
     *                    - part_descr_name=123 is the part of a description name. Only certificates, which contain
     *                    the value in their descriptions will be shown;
     *                    - sortByName=asc/desc is the parameter to sort all the certificates by name. ASC means
     *                    to sort in normal order, DESC - the sort order is reversed;
     *                    -  sortByDate=asc/desc is the parameter to sort all the certificates by CreateDate.
     *                    ASC means to sort in normal order, DESC - the sort order is reversed.
     * @return {@link List<User>} - all {@link User}s in the system.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<EntityModel<User>> fetchAllUsers(@RequestParam Map<String, String> parameters) {
        List<User> users = userService.findAllUsers(parameters);
        List<EntityModel<User>> moderFromOrders = users.stream().map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).userOrders(user.getId()))
                                .withRel("Fetches all orders this user: GET")))
                .collect(Collectors.toList());
        return CollectionModel.of(moderFromOrders, linkTo(methodOn(OrderController.class).fetchAllOrders()).withSelfRel(),
                linkTo(methodOn(OrderController.class).fetchAllOrders()).withRel("Fetches all orders: GET"),
                linkTo(methodOn(GiftCertificateController.class).certificates(new HashMap<>()))
                        .withRel("Fetches all certificates: GET"),
                linkTo(methodOn(CertificateTagController.class).tags(parameters)).withRel("Fetches all tags: GET"));
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
