package com.calorie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Global CORS cross‑origin resource sharing configuration
 * Defines permitted origins, HTTP methods, request headers, credential support and pre‑flight cache maxAge
 * Applies to all API endpoints
 */
@Configuration
public class CorsConfig {
    /**
     * Construct CORS configuration source bean for Spring Security
     * Allows local Angular dev server and auxiliary local origins, enables credentials for JWT authentication
     * Pre‑flight OPTIONS response cache expires after 3600 second
     *
     * @return configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow browser to send these headers in cross‑origin requests, includes Authorization for JWT
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept"
        ));
        // Enable credential mode: permits browser to send Authorization header under cross-origin
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}