package com.epam.esm.security;

import com.epam.esm.security.jwt.AuthEntryPointJwt;
import com.epam.esm.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
//for the annotation preAuthorize also. The annotation is used in @service over the method
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private final JwtFilter jwtFilter;
    @Autowired
    private final AuthEntryPointJwt unauthorizedHandler;
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/signout").authenticated()
                .antMatchers(HttpMethod.POST, "/users").anonymous() //can admin create user?
                .antMatchers(HttpMethod.GET, "/certificates", "/certificates/{\\d+}", "/tags", "/tags/{\\d+}").permitAll()
                .antMatchers(HttpMethod.POST, "/orders").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/orders", "/orders/{\\d+}", "/users", "/users/{\\d+}",
                        "/statistics/mostPopularTag", "/users/{\\d+}/orders").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.DELETE, "/tags/{\\d+}", "/certificates/{\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/tags/{\\d+}", "/certificates/{\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/tags/{\\d+}", "/certificates/{\\d+}").hasRole("ADMIN")
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
