package com.epam.esm.dto;

import com.epam.esm.model.impl.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseLoginDto {
    private String token;
    //    private String type = "Bearer";
    private Long id;
    private String nickname;
    private Role role;
}
