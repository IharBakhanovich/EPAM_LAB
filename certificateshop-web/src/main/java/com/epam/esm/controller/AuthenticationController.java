package com.epam.esm.controller;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dto.ResponseLoginDto;
import com.epam.esm.dto.UserDetailsDto;
import com.epam.esm.model.impl.User;
import com.epam.esm.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

/**
 * API to work with user authentication of the GiftCertificatesShop.
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class AuthenticationController {
    public static final String HEADER_NAME = "Authorization";
    public static final String HEADER_STARTS_WITH = "Bearer ";

    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtUtils jwtUtils;
    @Autowired
    private final Translator translator;

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
        // decoding the encoded UsernamePasswordAuthenticationToken
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
        jwtUtils.addTokenToLoginTokens(jwt);
        return ResponseEntity.ok(ResponseLoginDto.builder()
                .token(jwt)
                .id(userDetails.getId())
                .nickname(userDetails.getUsername())
                .role(userDetails.getRoles().stream().findAny().orElse(null))
                .build());
    }

    /**
     * The method that realises the 'POST /logout' query.
     *
     * @param request is the {@link HttpServletRequest}.
     * @return a String that is the answer to the {@link User}.
     */
    @PostMapping(value = "/signout")
    @ResponseStatus(HttpStatus.OK)
    public String logout(HttpServletRequest request) {
        String headerAuth = request.getHeader(HEADER_NAME);
        String authToken = null;
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(HEADER_STARTS_WITH)) {
            authToken = headerAuth.substring(7, headerAuth.length());
        }
        if (authToken == null) {
            return translator.toLocale("SOMETHING_WENT_WRONG");
        }
        jwtUtils.removeTokenFromLoginTokens(authToken);
        return translator.toLocale("YOU_ARE_LOGGED_OUT_YOUR_TOKEN_IS_NOT_MORE_VALID");
    }
}
