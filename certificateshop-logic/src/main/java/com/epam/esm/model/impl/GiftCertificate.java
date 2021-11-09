package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The POJO that describes the {@link GiftCertificate} entity in the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Certificate")
@Table(
        schema = "certificates",
        name = "gift_certificate"
)
//@NamedNativeQuery(
//        name = "find_all_certificates_pagination",
//        query =
//                "select c.id as certificateId, c.name as certificateName," +
//                        " c.description as certificateDescription, c.duration as certificateDuration," +
//                        " c.create_date as certificateCreateDate, c.price as certificatePrice," +
//                        " c.last_update_date as certificateLastUpdateDate, t.id as tagId, t.name as tagName" +
//                        " from gift_certificate as c LEFT OUTER JOIN (has_tag as h LEFT OUTER JOIN tag as t ON t.id = h.tagId)" +
//                        " ON c.id = h.certificateId WHERE c.id IN (select * from (select id from gift_certificate order by id" +
//                        " LIMIT ?, ?) as query1)",
//        resultSetMapping = "certificate_with_tags"
//)
//@SqlResultSetMapping(
//        name = "certificate_with_tags",
//        entities = {
//                @EntityResult(
//                        entityClass = GiftCertificate.class,
//                        fields = {
//                                @FieldResult( name = "id", column = "certificateId" ),
//                                @FieldResult( name = "name", column = "certificateName" ),
//                                @FieldResult( name = "description", column = "certificateDescription" ),
//                                @FieldResult( name = "price", column = "certificatePrice" ),
//                                @FieldResult( name = "duration", column = "certificateDuration" ),
//                                @FieldResult( name = "create_date", column = "certificateCreateDate" ),
//                                @FieldResult( name = "last_update_date", column = "certificateLastUpdateDate" ),
//                        }
//                ),
//                @EntityResult(
//                        entityClass = CertificateTag.class,
//                        fields = {
//                                @FieldResult( name = "id", column = "tagId" ),
//                                @FieldResult( name = "name", column = "tagName" ),
//                        }
//                )
//        }
//)
//@Synchronize({"gift_certificate", "has_tag", "tag"})
public class GiftCertificate implements DatabaseEntity, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;
    @Column(name = "name", unique = true, nullable = false)
    @NotNull
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "duration")
    private long duration;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Column(name = "create_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Column(name = "last_update_date")
    @UpdateTimestamp
    private LocalDateTime lastUpdateDate;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "has_tag",
            joinColumns = {@JoinColumn(name = "certificateid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tagid", referencedColumnName = "id")}
    )
    private List<CertificateTag> tags;
}
