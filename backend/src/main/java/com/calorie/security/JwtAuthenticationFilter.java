package com.calorie.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter executed once per incoming HTTP request
 * Extracts Bearer token from Authorization header, validates JWT and populates Spring
 * Security context with user identity when token is valid
 * Skips processing for CORS OPTIONS preflight requests
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    /**
     * Processing logic for each request
     * Skips processing for CORS OPTIONS preflight requests. Extracts JWT token, validates signature and expiration,
     * builds authentication object and sets it into SecurityContextHolder if token is valid
     * Any parsing or validation exceptions are logged without interruption filter chain flow
     *
     * @param request incoming http request
     * @param response outgoing http response
     * @param filterChain Spring security filter chain
     * @throws ServletException servlet processing error
     * @throws IOException IO error during request-response handling
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Skips JWT authentication for browser CORS pre-flight OPTIONS request
        // Pre-flight request does not contain Authorization Bearer token
        // Pass down filter chain for CORS header processing
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts raw JWT string from Authorization Bearer token
     * Expects header format: Bearer [jwt-token]
     *
     * @param request incoming http request
     * @return raw JWT token string, null if header missing or format mismatch
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}