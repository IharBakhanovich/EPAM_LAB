package com.epam.esm.model.impl;

import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.model.DatabaseEntity;

import java.util.Arrays;
import java.util.List;

public enum Role implements DatabaseEntity {
    UNAUTHORIZED(0L),
    ADMIN(1L),
    USER(2L);
    public static final String ROLE_NOT_VALID = "05";
    /**
     * creates the {@link List <Role>} with all the values of the {@link Role}.
     */
    public static final List<Role> ALL_AVAILABLE_ROLES = Arrays.asList(values());
    private final Long id;

    /**
     * Constructs a new {@link Role}.
     *
     * @param id is the value of the id of the new {@link Role}
     */
    Role(Long id) {
        this.id = id;
    }

    /**
     * Returns the List of all the Role values.
     *
     * @return {@link List<Role>}.
     */
    public static List<Role> valuesAsList() {
        return ALL_AVAILABLE_ROLES;
    }

    /**
     * Returns Role by id.
     *
     * @param id is the id to search.
     * @return {@link Role} with the id equals the {@param id}.
     * @throws EntityNotFoundException if such id does not exist.
     */
    public static Role resolveRoleById(Long id) {
        for (Role role : Role.values()
        ) {
            if (role.id.equals(id)) {
                return role;
            }
        }
        throw new EntityNotFoundException(ROLE_NOT_VALID, String.format("There is no Role with the id = %s", id));
    }

    /**
     * A Getter for the name.
     *
     * @return The name
     */
    public String getName() {
        return this.name();
    }

    /**
     * A Getter for the id.
     *
     * @return The id.
     */
    public Long getId() {
        return this.id;
    }


}
