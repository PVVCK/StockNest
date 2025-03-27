package com.ecommerce.stocknest.interceptor;

import java.io.IOException;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ecommerce.stocknest.security.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTInterceptor implements HandlerInterceptor {

    private final JWTUtil jwtUtil;

    // Paths restricted for ROLE_USER (only GET allowed)
    private static final Set<String> RESTRICTED_PATHS = Set.of(
        "/stocknest/product/",
        "/stocknest/image/",
        "/stocknest/category/"
    );

    public JWTInterceptor(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        String authHeader = request.getHeader("Authorization");
        String requestPath = request.getServletPath();
        String method = request.getMethod();

        // Allow authentication APIs without token
        if (requestPath.startsWith("/stocknest/auth/")) {
            return true; // Allow without authentication
        }

        // If no token is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error: Missing or invalid token");
            return false;
        }

        // Extract token
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error: Invalid or expired token");
            return false;
        }

        // Extract user details
        String role = jwtUtil.getRole(token);  

        // Restrict "ROLE_USER" from modifying products, images, and categories (only GET is allowed)
        if (role.equals("ROLE_USER") && RESTRICTED_PATHS.stream().anyMatch(requestPath::startsWith)
                && !HttpMethod.GET.matches(method)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Error: Users can only view products, images, and categories");
            return false;
        }

        return true; // Allow request to proceed
    }
}
