package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

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
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name="role", nullable = false)
    private Role role;
}
