package com.epam.esm.controller;

import com.epam.esm.dao.impl.ColumnNames;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.OrderService;
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
 * API to work with {@link Order}s of the GiftCertificatesShop.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    /**
     * Constructs the {@link OrderController}.
     *
     * @param orderService is the service to inject.
     */
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * The method that realises the 'POST /users/{userId}/{certificateId}' query
     * and orders {@link GiftCertificate} by {@link User}.
     *
     * @param userId is the ID of the {@link User} to find.
     * @return {@link List <Order>} that belong ti the {@link User} with the id equals userId.
     */
    @PostMapping(value = "/{userId}/{certificateId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Order> orderCertificate(@PathVariable("userId") long userId,
                                               @PathVariable("certificateId") long certificateId) {
        Order order = orderService.orderCertificate(userId, certificateId);
        EntityModel<Order> orderEntityModel = EntityModel.of(order, linkTo(methodOn(OrderController.class)
                .fetchOrderById(order.getId()))
                .withRel("Fetches order by orderId (inputs: orderId): GET"));
        orderEntityModel.add(linkTo(methodOn(UserController.class).userOrders(userId))
                .withRel("Fetches all orders this user (inputs: userId): GET"));
        orderEntityModel.add(linkTo(methodOn(OrderController.class).addNewOrder(new Order()))
                .withRel("Creates new order (inputs: new order object): POST"));
        orderEntityModel.add(linkTo(methodOn(GiftCertificateController.class).certificates(new HashMap<>()))
                .withRel("Fetches all certificates from the system (inputs: params(see API docs)): GET"));

        return orderEntityModel.add(linkTo(methodOn(OrderController.class).orderCertificate(userId, certificateId))
                .withSelfRel());
    }

    /**
     * The method that realises the 'GET /orders/{orderId}' query.
     *
     * @param orderId is the ID of the {@link Order} to find.
     * @return {@link GiftCertificate} with the certificateId if such an id exists in the system.
     */
    @GetMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Order> fetchOrderById(@PathVariable("orderId") long orderId) {

        Order order = orderService.findOrderById(orderId);
        EntityModel<Order> orderEntityModel = EntityModel.of(order, linkTo(methodOn(OrderController.class)
                .orderCertificate(order.getUser().getId(), order.getCertificates().get(0).getId()))
                .withRel("Orders certificate by user(inputs: userId/certofocateId): POST"));
        orderEntityModel.add(linkTo(methodOn(OrderController.class).addNewOrder(new Order()))
                .withRel("Creates new order (inputs: new order object): POST"));
        orderEntityModel.add(linkTo(methodOn(OrderController.class).fetchAllOrders(ColumnNames.DEFAULT_PARAMS))
                .withRel("Fetches all orders: GET"));

        return orderEntityModel.add(linkTo(methodOn(OrderController.class).fetchOrderById(orderId)).withSelfRel());
    }

    /**
     * The method that realises the 'GET /orders' query.
     *
     * @param parameters: there are following parameters, which can be applied to the query:
     *                    -  offset=0/MAX_VALUE is the long to pass records from database.
     *                    To fetch records from 6 record 'offset' should be set to 5.
     *                    - limit = 0/MAX_VALUE is the long to set how many records should be fetched.
     * @return {@link List<User>} - all {@link User}s in the system.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<EntityModel<Order>> fetchAllOrders(@RequestParam Map<String, String> parameters) {
        parameters = ColumnNames.validateParameters(parameters, ColumnNames.DEFAULT_ENTITIES_ON_THE_PAGE);
        List<Order> orders = orderService.findAllOrders(parameters);
        long offset = Long.parseLong(parameters.get("offset"));
        long limit = Long.parseLong(parameters.get("limit"));
        Map<String, String> paramsNext = ColumnNames.createNextParameters(orders, offset, limit);
        Map<String,String> paramsPrev = ColumnNames.createPrevParameters(orders, offset, limit);
        List<EntityModel<Order>> moderFromOrders = orders.stream().map(order -> EntityModel.of(order,
                        linkTo(methodOn(OrderController.class).fetchOrderById(order.getId())).
                                withRel("Fetches and removes order from the system (params: orderId): GET, DELETE")))
                .collect(Collectors.toList());
        return CollectionModel.of(moderFromOrders,
                linkTo(methodOn(UserController.class).fetchAllUsers(new HashMap<String, String>()))
                        .withRel("Fetches all users: GET"),
                linkTo(methodOn(GiftCertificateController.class).certificates(ColumnNames.DEFAULT_PARAMS))
                        .withRel("Fetches all certificates: GET"),
                linkTo(methodOn(CertificateTagController.class).tags(ColumnNames.DEFAULT_PARAMS))
                        .withRel("Fetches all tags: GET"),
                linkTo(methodOn(OrderController.class).fetchAllOrders(paramsPrev))
                        .withRel("Fetches PREVIOUS PAGE of order: GET"),
                linkTo(methodOn(OrderController.class).fetchAllOrders(paramsNext))
                        .withRel("Fetches NEXT PAGE of orders: GET"),
                linkTo(methodOn(OrderController.class).fetchAllOrders(parameters)).withSelfRel());
    }

    /**
     * The method that realises the 'POST /orders' query.
     *
     * @param order is the {@link CertificateTag} to create.
     * @return the created {@link CertificateTag}.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Order> addNewOrder(@RequestBody Order order) {
        Order orderAfterAdd = orderService.createOrder(order);
        EntityModel<Order> orderEntityModel = EntityModel.of(orderAfterAdd, linkTo(methodOn(OrderController.class)
                .orderCertificate(order.getUser().getId(), order.getCertificates().get(0).getId()))
                .withRel("Orders certificate by user(inputs: userId/certofocateId): POST"));
        orderEntityModel.add(linkTo(methodOn(OrderController.class).fetchOrderById(orderAfterAdd.getId()))
                .withRel("Fetches order by id (params: orderId : GET"));
        orderEntityModel.add(linkTo(methodOn(OrderController.class).fetchAllOrders(ColumnNames.DEFAULT_PARAMS))
                .withRel("Fetches all orders: GET"));
        return orderEntityModel.add(linkTo(methodOn(OrderController.class).addNewOrder(new Order()))
                .withSelfRel());
    }
}
