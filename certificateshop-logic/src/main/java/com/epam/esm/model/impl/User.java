package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * The POJO that describes the {@link User} entity in the system.
 */
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user")
@Table(
        schema = "certificates",
        name = "user"
)
public class User implements DatabaseEntity, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;
    @Column(name = "nickname", unique = true, nullable = false)
    private String nickName;
//    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
//    private List<Order> orders;
}
