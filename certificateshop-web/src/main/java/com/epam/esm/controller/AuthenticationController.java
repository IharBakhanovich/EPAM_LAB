package com.epam.esm.controller;

import com.epam.esm.dto.ResponseLoginDto;
import com.epam.esm.dto.UserDetailsDto;
import com.epam.esm.model.impl.User;
import com.epam.esm.security.jwt.JwtUtils;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

/**
 * API to work with user authentication of the GiftCertificatesShop.
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * The method that realises the 'POST /login' query.
     *
     * @param authenticationEncoded is the encoded credentials of the authenticated {@link User}.
     * @return {@link ResponseLoginDto} with the JwtToken, username, id and role of the authenticated {@link User}.
     */
    @PostMapping(value = "/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseLoginDto> loginUser(
            @RequestHeader(name = "Authorization") String authenticationEncoded) {
        // decoding the encoded string
        String encoded = authenticationEncoded.substring(6);
        byte[] decoded = Base64.getDecoder().decode(encoded);
        String stringDecoded = new String(decoded);
        String[] userNameAndPassword = stringDecoded.split(":");
        String email = userNameAndPassword[0];
        String password = userNameAndPassword[1];

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsDto userDetails = (UserDetailsDto) authentication.getPrincipal();
        return ResponseEntity.ok(ResponseLoginDto.builder()
                .token(jwt)
                .id(userDetails.getId())
                .nickname(userDetails.getUsername())
                .role(userDetails.getRoles().stream().findAny().orElse(null))
                .build());
    }

//    /**
//     * The method that realises the 'POST /logout' query.
//     *
//     * @param requestLoginDto is the Dto with nicknameID and password of the {@link User} that is authenticated.
//     * @return {@link ResponseLoginDto} with the JwtToken, username, id and role.
//     */
//    @GetMapping(value = "/logout")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<ResponseLoginDto> logout(RequestLoginDto requestLoginDto) {
//
//    }


}
