package com.epam.esm.dao.impl.jpa;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The class that implements the {@link OrderDao} interface.
 */
@Profile("dev_jpa")
@Repository
@Transactional
public class JpaOrderDaoImpl implements OrderDao {
    private OrderRepository orderRepository;

    @Autowired
    public JpaOrderDaoImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Saves {@link Order} in the database.
     *
     * @param order is the {@link Order} to save.
     */
    @Override
    public void save(Order order) {

    }

    /**
     * Finds all {@link Order} entity in the database.
     *
     * @return List of the {@link Order} objects.
     */
    @Override
    public List<Order> findAll() {
        return null;
    }

    /**
     * Finds {@link Optional <Order>} in the database by the id of the {@link Order}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<Order>}.
     */
    @Override
    public Optional<Order> findById(long id) {
        return Optional.empty();
    }

    /**
     * Updates the {@link Order}.
     *
     * @param order is the value of the {@link Order} to update.
     */
    @Override
    public void update(Order order) {

    }

    /**
     * Removes the {@link Order} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {

    }

    /**
     * Finds {@link Optional<Order>} in the database by the id of the {@link Order}.
     *
     * @param name is the {@link long} to find.
     * @return {@link Optional<Order>}.
     */
    @Override
    public Optional<Order> findByName(String name) {
        return Optional.empty();
    }

    /**
     * Saves orderId, certificateId and giftCertificate in JSON in the 'userorder_certificate' table of the database.
     *
     * @param orderId          is the id of the {@link Order} to save.
     * @param certificateId    is the id of the {@link GiftCertificate} to save.
     * @param giftCertificate  is the {@link GiftCertificate} to save in JSON format.
     * @param certificatePrice is the {@link BigDecimal} to save as a price of the {@param giftCertificate}.
     */
    @Override
    public void saveIdsInUserorder_certificateTable(long orderId, long certificateId, GiftCertificate giftCertificate, BigDecimal certificatePrice) {

    }

    /**
     * Finds all {@link Order} entity in the database.
     *
     * @param pageNumber              is the pageNumber query parameter.
     * @param amountEntitiesOnThePage is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link Order} objects.
     */
    @Override
    public List<Order> findAllPagination(int pageNumber, int amountEntitiesOnThePage) {
        return null;
    }
}
