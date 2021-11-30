//package com.epam.esm.security;
//
//import com.epam.esm.security.jwt.JwtFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
////@EnableWebSecurity
////@EnableGlobalMethodSecurity(prePostEnabled = true) //for the annotation preAuthorize also. The annotation is used in @service over the method
////@Configuration
//public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
////    private final JwtFilter jwtFilter;
////
////    @Autowired
////    public SpringSecurityConfig(JwtFilter jwtFilter) {
////        this.jwtFilter = jwtFilter;
////    }

//@Autowired
//private UserDetailsService userDetailsService;
////
////    @Bean
////    @Override
////    public AuthenticationManager authenticationManagerBean() throws Exception {
////        return super.authenticationManagerBean();
////    }
////
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }

//@Override
//protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(userPasswordEncoder);
//        }
////
////    @Override
////    protected void configure(HttpSecurity httpSecurity) throws Exception {
////        httpSecurity
////                .httpBasic(AbstractHttpConfigurer::disable)
////                .csrf(AbstractHttpConfigurer::disable)
////                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .authorizeRequests()
////                .antMatchers(HttpMethod.POST, "/login", "/signup").anonymous()
////                .antMatchers(HttpMethod.POST, "/logout").authenticated()
////                .antMatchers(HttpMethod.GET, "/certificates", "/certificates/{\\d+}", "/users", "/users/{\\d+}").anonymous();
//////                .and()
//////                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
////    }
//
//}
