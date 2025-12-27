package com.userManagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(TOKEN_PREFIX.length());

        String username = null;
        try {
            username = jwtUtil.extractUsername(token);
        }catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            if (jwtUtil.isTokenValid(token, username)){
                String role = "USER";

                try{
                    Object claimedRole = jwtUtil.extractClaims(token, claims -> claims.get("role"));
                    if (claimedRole != null){
                        role = claimedRole.toString();
                    }
                } catch (Exception e){
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_"+role)));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // can we return or throw an error here
            }
        }
        filterChain.doFilter(request, response);
    }
}
