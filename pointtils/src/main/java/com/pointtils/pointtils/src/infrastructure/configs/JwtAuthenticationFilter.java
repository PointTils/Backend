package com.pointtils.pointtils.src.infrastructure.configs;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RedisBlacklistService redisBlacklistService;

    public JwtAuthenticationFilter(JwtService jwtService, RedisBlacklistService redisBlacklistService) {
        this.jwtService = jwtService;
        this.redisBlacklistService = redisBlacklistService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (redisBlacklistService.isBlacklisted(authHeader.substring(7))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido");
            return;
        }

        try {
            final String jwt = authHeader.substring(7);

            if (jwtService.isTokenExpired(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized.");
                return;
            }

            String subject = jwtService.extractClaim(jwt, claims -> claims.getSubject());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized.");
        }
    }
}