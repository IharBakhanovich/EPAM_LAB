package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import lombok.*;

import java.util.List;

/**
 * The POJO that describes the {@link User} entity in the system.
 */
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements DatabaseEntity {
    private long id;
    private String nickName;
    //private List<Order> orders;
}
