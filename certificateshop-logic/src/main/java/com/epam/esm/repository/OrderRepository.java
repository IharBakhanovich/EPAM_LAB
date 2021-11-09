package com.epam.esm.repository;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface OrderRepository extends CrudRepository<Order, Long>, JpaRepository<Order, Long> {
}
