package com.epam.esm.dao;

import com.epam.esm.exception.DuplicateException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderDao extends Dao<Order> {
    /**
     * Saves {@link Order} in the database.
     *
     * @param order is the {@link Order} to save.
     */
    @Override
    void save(Order order);

    /**
     * Finds all {@link Order} entity in the database.
     *
     * @return List of the {@link Order} objects.
     */
    @Override
    List<Order> findAll();

    /**
     * Finds {@link Optional<Order>} in the database by the id of the {@link Order}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<Order>}.
     */
    @Override
    Optional<Order> findById(long id);

    /**
     * Updates the {@link Order}.
     *
     * @param order is the value of the {@link Order} to update.
     */
    @Override
    void update(Order order);

    /**
     * Removes the {@link Order} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    void delete(long id);

    /**
     * Finds {@link Optional<Order>} in the database by the id of the {@link Order}.
     *
     * @param name is the {@link String} to find.
     * @return {@link Optional<Order>}.
     */
    Optional<Order> findByName(String name);

    /**
     * Saves orderId, certificateId and giftCertificate in JSON in the 'userorder_certificate' table of the database.
     *
     * @param orderId         is the id of the {@link Order} to save.
     * @param certificateId   is the id of the {@link GiftCertificate} to save.
     * @param giftCertificate is the {@link GiftCertificate} to save in JSON format.
     * @param certificatePrice is the {@link BigDecimal} to save as a price of the {@param giftCertificate}.
     */
    void saveIdsInUserorder_certificateTable(long orderId, long certificateId,
                                             GiftCertificate giftCertificate, BigDecimal certificatePrice);

    /**
     * Finds all {@link Order} entity in the database.
     *
     * @param pageNumber is the pageNumber query parameter.
     * @param amountEntitiesOnThePage  is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link Order} objects.
     */
    List<Order> findAllPagination(int pageNumber, int amountEntitiesOnThePage);

    /**
     * Finds all {@link Order} entity in the database which belongs to the {@link User} with the ID equals {@param userId}.
     *
     * @param userId is the ID of the {@link User} which orders is to fetch.
     * @return List of the {@link Order} objects.
     */
    List<Order> findAllByUserId(long userId);
}
