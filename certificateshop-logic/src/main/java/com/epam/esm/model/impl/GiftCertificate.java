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
//        schema = "certificates",
        name = "gift_certificate"
)
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
