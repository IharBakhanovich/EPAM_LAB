package com.epam.esm.repository;

import com.epam.esm.model.impl.CertificateTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateTagRepository extends CrudRepository<CertificateTag, Long>,
       JpaRepository<CertificateTag, Long> {
    public static final String FIND_MOST_POPULAR_TAG_BY_THE_BEST_USER =
            "SELECT tag.id as tagId, tag.name as tagName FROM tag" +
                    " where id = (SELECT query3.tagIdAfterCount" +
                    " FROM (SELECT ht.tagId as tagIdAfterCount, count(ht.tagId) as amountOfTag" +
                    " FROM userorder as uo LEFT OUTER JOIN" +
                    " (has_tag as ht LEFT OUTER JOIN userorder_certificate as uoc on ht.certificateId = uoc.certificateId)" +
                    " ON uo.id = uoc.userOrderId" +
                    " WHERE uo.userId = (SELECT query1.id" +
                    " FROM (SELECT uo.userId as id, SUM(uoc.certificatePrice) as costAllCertificates" +
                    " FROM userorder_certificate as uoc LEFT OUTER JOIN userorder as uo ON uoc.userOrderId = uo.id" +
                    " GROUP BY userId ORDER BY costAllCertificates DESC) as query1 LIMIT 1)" +
                    " GROUP BY ht.tagId ORDER BY amountOfTag DESC) as query3 LIMIT 1)";

    @Modifying
    @Query("update Tag t set t.name = ?1 where t.id = ?2")
    void update(String name, long id);

    @Query("select t from Tag t where t.name = :name")
    Optional<CertificateTag> findCertificateTagByName(@Param("name") String name);

    @Query("select t from Tag t, Certificate c join c.tags where c.id = :certificateId")
    List<CertificateTag> findCertificateTagsByCertificateId(@Param("certificateId") long id);

}
