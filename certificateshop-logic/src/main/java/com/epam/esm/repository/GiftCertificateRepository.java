package com.epam.esm.repository;

import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface GiftCertificateRepository extends CrudRepository<GiftCertificate, Long>,
        JpaRepository<GiftCertificate, Long> {
    Optional<GiftCertificate> findByName(String name);
}
