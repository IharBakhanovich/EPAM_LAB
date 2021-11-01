package com.epam.esm.controller;

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
        orderEntityModel.add(linkTo(methodOn(OrderController.class).fetchAllOrders())
                .withRel("Fetches all orders: GET"));

        return orderEntityModel.add(linkTo(methodOn(OrderController.class).fetchOrderById(orderId)).withSelfRel());
    }

    /**
     * The method that realises the 'GET /orders' query.
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
    public CollectionModel<EntityModel<Order>> fetchAllOrders() {
//        Long offset = Long.getLong(parameters.get("offset"));
//        Long limit = Long.getLong(parameters.get("limit"));

//        if (offset == null || limit == null) {
//            parameters.put("offset", "0");
//            parameters.put("limit", "20");
//            return orderService.findAllOrders(parameters);
//        }
        List<Order> orders = orderService.findAllOrders();
        List<EntityModel<Order>> moderFromOrders = orders.stream().map(order -> EntityModel.of(order,
                        linkTo(methodOn(OrderController.class).fetchOrderById(order.getId())).withSelfRel(),
                        linkTo(methodOn(OrderController.class).fetchAllOrders()).withRel("Fetches all orders: GET")))
                .collect(Collectors.toList());
        return CollectionModel.of(moderFromOrders, linkTo(methodOn(OrderController.class).fetchAllOrders()).withSelfRel(),
                linkTo(methodOn(UserController.class).fetchAllUsers(new HashMap<String, String>()))
                        .withRel("Fetches all users: GET"),
                linkTo(methodOn(GiftCertificateController.class).certificates(new HashMap<>()))
                        .withRel("Fetches all certificates: GET"),
                linkTo(methodOn(CertificateTagController.class).tags()).withRel("Fetches all tags: GET"));
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
        orderEntityModel.add(linkTo(methodOn(OrderController.class).fetchAllOrders())
                .withRel("Fetches all orders: GET"));
        return orderEntityModel.add(linkTo(methodOn(OrderController.class).addNewOrder(new Order()))
                .withSelfRel());
    }
}
