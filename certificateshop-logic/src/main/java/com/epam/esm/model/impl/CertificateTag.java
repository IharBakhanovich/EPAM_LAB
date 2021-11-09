package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * The POJO that describes the CertificateTag.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Tag")
@Table(
        schema = "certificates",
        name = "tag"
)
//@NamedNativeQueries(value = {
//        @NamedNativeQuery(name = "tag.findAllPagination",
//                query = "select tag.id as tagId, tag.name as tagName from tag" +
//                        " WHERE tag.id IN (select * from (select id from tag order by id LIMIT ?, ?) as query1)",
//        resultClass = CertificateTag.class)
//})
public class CertificateTag implements DatabaseEntity, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false)
    private long id;
    @Column(name = "name", unique = true, nullable = false)
    @NotNull
    private String name;
}
