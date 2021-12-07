package com.epam.esm.dao.impl.jpa;

import com.epam.esm.dao.ListToResultSetConverter;
import com.epam.esm.dao.UserDao;
import com.epam.esm.model.impl.User;
import com.epam.esm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The class that implements the {@link UserDao} interface.
 */
@Profile("dev_jpa")
@Repository
@Transactional
public class JpaUserDaoImpl implements UserDao {
    private static final String INSERT_ENTITY_SQL = "insert into user (nickName, password, role) values (?, ?, ?)";
    private static final List<String> USER_HEADERS = Arrays.asList("userId", "userNickName", "userOrderId",
            "orderCreateDate", "orderName", "orderCertificate");
    private UserRepository userRepository;
    private EntityManager entityManager;
    private ListToResultSetConverter listToResultSetConverter;

    @Autowired
    public JpaUserDaoImpl(UserRepository userRepository, EntityManager entityManager,
                          ListToResultSetConverter resultListToResultSetConverter) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.listToResultSetConverter = resultListToResultSetConverter;
    }

    /**
     * Saves {@link User} in the database.
     *
     * @param user is the {@link User} to save.
     */
    @Override
    public void save(User user) {
        entityManager.persist(user);
    }

    /**
     * Finds all {@link User} entity in the database.
     *
     * @return List of the {@link User} objects.
     */
    @Override
    public List<User> findAll() {
        return entityManager.createQuery("select u from user u order by u.id", User.class).getResultList();
    }

    /**
     * Finds {@link Optional <User>} in the database by the id of the {@link User}.
     *
     * @param id is the {@link long} to find.
     * @return {@link Optional<User>}.
     */
    @Override
    public Optional<User> findById(long id) {
        Optional<User> user = entityManager
                .createQuery("select u from user u where u.id = :id", User.class)
                .setParameter("id", id).getResultList().stream().findFirst();
        return user;
    }

    /**
     * Updates the {@link User}.
     *
     * @param user is the value of the {@link User} to update.
     */
    @Override
    public void update(User user) {
        User user1 = entityManager.find(User.class, user.getId());
        entityManager.createQuery("UPDATE user u set u.nickName = :nickName, u.password = :password, u.role = :role where u.id = :id")
                .setParameter("nickName", user.getNickName())
                .setParameter("password", user.getPassword())
                .setParameter("role", user.getRole().getId())
                .setParameter("id", user.getId()).executeUpdate();
        entityManager.refresh(user1);
    }

    /**
     * Deletes the {@link User} object from the database.
     *
     * @param id is the value of the {@link long} to find.
     */
    @Override
    public void delete(long id) {
        entityManager
                .createQuery("delete from user u where u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    /**
     * Finds {@link Optional<User>} in the database by the id of the {@link User}.
     *
     * @param nickName is the {@link long} to find.
     * @return {@link Optional<User>}.
     */
    @Override
    public Optional<User> findByName(String nickName) {
        return entityManager
                .createQuery("select u from user u where u.nickName = :name", User.class)
                .setParameter("name", nickName).getResultList().stream().findFirst();
    }

    /**
     * Finds all {@link User} entity in the database.
     *
     * @param pageNumber              is the pageNumber query parameter.
     * @param amountEntitiesOnThePage is the amountEntitiesOnThePage query parameter.
     * @return List of the {@link User} objects.
     */
    @Override
    public List<User> findAllPagination(int pageNumber, int amountEntitiesOnThePage) {
        return entityManager.createQuery("select u from user u order by u.id", User.class)
                .setFirstResult(amountEntitiesOnThePage * pageNumber)
                .setMaxResults(amountEntitiesOnThePage)
                .getResultList();
    }
}
