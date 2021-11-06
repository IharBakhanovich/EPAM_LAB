package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import lombok.*;

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
public class User implements DatabaseEntity, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;
    @Column(name = "nickname", unique = true, nullable = false)
    private String nickName;
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}
