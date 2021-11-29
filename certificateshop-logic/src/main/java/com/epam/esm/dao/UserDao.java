package com.epam.esm.dao;

import com.epam.esm.exception.DuplicateException;
import com.epam.esm.model.impl.User;

import java.util.List;
import java.util.Optional;

public interface UserDao extends Dao<User> {
    /**
     * Saves {@link User} in the database.
     *
     * @param user is the {@link User} to save.
     * @throws DuplicateException if a SQLException with the state 23505 or the state 23000 is thrown.
     */
    @Override
    void save(User user);

    /**
     * Finds all {@link User} entity in the database.
     *
     * @return List of the {@link User} objects.
     */
    @Override
    List<User> findAll();

    /**
     * Finds {@link Optional<User>} in the database by the id of the {@link User}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<User>}.
     */
    @Override
    Optional<User> findById(long id);

    /**
     * Updates the {@link User}.
     *
     * @param user is the value of the {@link User} to update.
     */
    @Override
    void update(User user);

    /**
     * Deletes the {@link User} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    void delete(long id);

    /**
     * Finds {@link Optional<User>} in the database by the id of the {@link User}.
     *
     * @param nickName is the {@link long} to find.
     * @return {@link Optional<User>}.
     */
    Optional<User> findByName(String nickName);

    /**
     * Finds all {@link User} entity in the database.
     *
     * @param pageNumber              is the pageNumber query parameter.
     * @param amountEntitiesOnThePage is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link User} objects.
     */
    List<User> findAllPagination(int pageNumber, int amountEntitiesOnThePage);
}
