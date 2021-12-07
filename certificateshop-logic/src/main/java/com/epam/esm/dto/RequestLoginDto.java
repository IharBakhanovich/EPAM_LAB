package com.epam.esm.dto;

import com.epam.esm.model.impl.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestLoginDto {
    private String username;
    private String password;
}
