package com.epam.esm.service;

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
    User findUserById(long id);

    /**
     * Returns all {@link User}s in the system.
     *
     * @param parameters is all the query parameters in the URI.
     * @return {@link List<User>}, that represents all the users in the system.
     */
    List<User> findAllUsers(Map<String, String> parameters);
}
