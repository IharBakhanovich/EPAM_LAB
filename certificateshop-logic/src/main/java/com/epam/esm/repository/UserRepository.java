package com.epam.esm.repository;

import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Long>,
        JpaRepository<User, Long> {
}
