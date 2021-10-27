package com.epam.esm.controller;

import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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
    public List<Order> certificate(@PathVariable("userId") long userId) {
        return userService.findUserById(userId).getOrders();
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
    public List<User> fetchAllUsers(@RequestParam Map<String, String> parameters) {
        return userService.findAllUsers(parameters);
    }
}
