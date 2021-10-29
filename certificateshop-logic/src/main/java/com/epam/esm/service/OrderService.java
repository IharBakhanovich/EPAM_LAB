package com.epam.esm.service;

import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;

import java.util.List;
import java.util.Map;

/**
 * The interface that defines the {@link Order} api of the application.
 */
public interface OrderService {
    /**
     * Creates a new {@link Order} in the system.
     *
     * @param order is the {@link Order} to create.
     * @return created {@link Order}
     */
    Order createOrder(Order order);

    /**
     * Orders a {@link GiftCertificate} to the {@link User}
     *
     * @param userId        is the id of the {@link User} that order a {@param giftCertificate}.
     * @param certificateId is the id of the {@link GiftCertificate} that is ordered by {@param user}.
     * @return {@link Order}.
     */
    Order orderCertificate(long userId, long certificateId);

    /**
     * Generates name of an Order.
     *
     * @return generated name, that is unique in the system.
     */
    String generateUniqueOrderName(User user);

    /**
     * Returns a {@link Order} by its id.
     *
     * @param id is the id to find in the system.
     */
    Order findOrderById(long id);

    /**
     * Returns all {@link Order}s in the system.
     *
     * @param parameters is all the query parameters in the URI.
     * @return {@link List<Order>}, that represents all the orders in the system.
     */
    List<Order> findAllOrders(Map<String, String> parameters);
}
