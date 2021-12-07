package com.epam.esm.dto;

import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The DataTransferObject for the {@link Order} entity to transfer data outside the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    private String nickname;
    private Role role;
}
