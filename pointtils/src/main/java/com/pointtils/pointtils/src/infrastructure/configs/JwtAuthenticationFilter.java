package com.pointtils.pointtils.src.infrastructure.configs;

import java.io.IOException;
import java.util.ArrayList;

import io.jsonwebtoken.Claims;
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
    private final MemoryBlacklistService memoryBlacklistService;

    public JwtAuthenticationFilter(JwtService jwtService, MemoryBlacklistService memoryBlacklistService) {
        this.jwtService = jwtService;
        this.memoryBlacklistService = memoryBlacklistService;
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
        
        // Extrair token do header uma vez para evitar duplicação
        final String token = authHeader.substring(7);
        
        // Verificar se o token está na blacklist apenas se não for uma requisição de logout
        String requestURI = request.getRequestURI();
        if (!isLogoutEndpoint(requestURI) && memoryBlacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido");
            return;
        }

        try {
            if (jwtService.isTokenExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized.");
                return;
            }

            String subject = jwtService.extractClaim(token, Claims::getSubject);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized.");
        }
    }

    /**
     * Verifica se a URI da requisição é um endpoint de logout
     * Usa uma abordagem mais robusta para evitar vulnerabilidades de comparação hardcoded
     * e previne ataques de ReDoS (Regular Expression Denial of Service)
     */
    private boolean isLogoutEndpoint(String requestURI) {
        // Lista de endpoints de logout que devem permitir tokens blacklisted
        // Substitui a regex vulnerável por uma verificação mais segura
        return requestURI != null && (
            requestURI.equals("/v1/auth/logout") ||
            requestURI.startsWith("/v1/auth/logout/") ||
            requestURI.contains("/logout")
        );
    }
}
