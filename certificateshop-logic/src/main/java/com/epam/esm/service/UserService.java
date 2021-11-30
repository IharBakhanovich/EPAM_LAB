package com.epam.esm.service;

import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;

import java.util.List;
import java.util.Map;

/**
 * The interface that defines the {@link User} api of the application.
 */
public interface UserService {
    /**
     * Returns a {@link User} by its id.
     *
     * @param id is the id of the {@link User} to find in the system.
     */
    UserDto findUserById(long id);

    /**
     * Returns all {@link User}s in the system.
     *
     * @param parameters is all the query parameters in the URI.
     * @return {@link List<User>}, that represents all the users in the system.
     */
    List<UserDto> findAllUsers(Map<String, String> parameters);

    /**
     * Creates a new {@link User} in the system.
     *
     * @param user is the {@link User} to create.
     */
    UserDto createUser(User user);

    /**
     * Returns a {@link User} by its id.
     *
     * @param userId is the id to find in the system.
     */
    UserDto fetchUserById(long userId);

    /**
     * Return the price and the timestamp of a purchase of the user's {@link Order} with the {@param orderId},
     * that was purchased by {@link User} with the {@param userId}.
     *
     * @param userId is the {@link User} ID to find by.
     * @param orderId is the {@link Order} ID to find by.
     * @return {@link OrderDto}
     */
    OrderDto findUserOrderByOrderIdCostAndTime(long userId, long orderId);
}
