package com.mikhail.shop.cars.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/users/**").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/librerias_js/**", "/plantillas_html/**").permitAll()
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/users/**", "/api/cart/**", "/checkout/**", "/loginAdmin", "/admin/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
            );

        return http.build();
    }
}