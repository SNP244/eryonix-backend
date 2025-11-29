package com.eryonix.security;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
) throws ServletException, IOException {

    String path = request.getServletPath();
    System.out.println("📥 Incoming request: " + path);

    if (path.equals("/api/auth/login") || path.equals("/api/auth/signup") || path.equals("/api/users/signup")) {
        System.out.println("⏭️ Skipping JWT check for: " + path);
        filterChain.doFilter(request, response);
        return;
    }

    final String authHeader = request.getHeader("Authorization");
    String username = null;
    String jwt = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        jwt = authHeader.substring(7);
        try {
            username = jwtUtil.extractUsername(jwt);
            System.out.println(" Extracted username: " + username);
        } catch (Exception e) {
            System.out.println(" Invalid JWT: " + e.getMessage());
        }
    } else {
        System.out.println(" No Authorization header or invalid format");
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println(" Loaded user from DB: " + userDetails.getUsername());

            if (jwtUtil.validateToken(jwt, userDetails)) {
                System.out.println(" JWT is valid. Setting authentication in context");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println(" JWT validation failed");
            }
        } catch (Exception e) {
            System.out.println(" Error authenticating user: " + e.getMessage());
        }
    } else {
        System.out.println(" Authentication already present or username null");
    }


    filterChain.doFilter(request, response);
}
}
