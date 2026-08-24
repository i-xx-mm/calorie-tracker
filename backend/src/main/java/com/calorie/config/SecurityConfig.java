package com.calorie.config;

import com.calorie.security.JwtAuthenticationFilter;
import com.calorie.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security main configuration
 * Configures stateless JWT authentication, public open endpoints, password hashing and filter chain ordering
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Password encoder bean using BCrypt hashing algorithm
     * Used for secure password storage during account registration and login verification
     *
     * @return Bcrypt password encoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Build custom JWT authentication filter bean
     * This filter parses and validates JWT Bearer token from HTTP request header
     *
     * @return instance of JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    /**
     * Main security filter chain definition
     *
     * Enable global CORS from CorsConfig
     * Disable DSRF protection for stateless JWT API
     * Set session policy to STATELESS. no server-side session will be created
     * Permit unauthenticated access to auth routes and health check endpoint
     * All remaining endpoints require valid authenticated JWT token
     * Insert custom JWT filter before default username-password filter
     *
     * @param http Spring security builder
     * @return built SecurityFilterChain
     * @throws Exception SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}