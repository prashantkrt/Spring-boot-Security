package com.mylearning.springsecurityjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(request -> request.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails user1 = User.builder()
                .username("user1")
                .password("{noop}password")
                .roles("USER")
                .build();

        UserDetails user2 = User.withUsername("user2")
                .password("{noop}password")
                .roles("USER")
                .build();

        UserDetails user3 = User.builder()
                .username("user3")
                .password("{noop}password")
                .roles("USER", "ADMIN")  // Will automatically set int the authorities ROLE_ADMIN and ROLE_USER
                .build();

        UserDetails user4 = User.builder()
                .username("user4")
                .password("{noop}password")
                .authorities("ROLE_USER", "ROLE_ADMIN") // if we add roles like USER and ADMIN, then it will Set as ROLE_USER and ROLE_ADMIN in authorities
                .build();

        return new InMemoryUserDetailsManager(user1, user2, user3);
    }
}
