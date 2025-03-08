package com.ecommerce.stocknest.security;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter { //OncePerRequestFilter Ensures that this filter executes only once per request.

	 private final JWTUtil jwtUtil;

	    // Paths where only GET is allowed for ROLE_USER
	    private static final Set<String> RESTRICTED_PATHS = Set.of(
	        "/stocknest/product/",
	        "/stocknest/image/",
	        "/stocknest/category/"
	    );

	    public JWTFilter(JWTUtil jwtUtil) {
	        this.jwtUtil = jwtUtil;
	    }

	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	            throws ServletException, IOException {

	    	String authHeader = request.getHeader("Authorization");
	        String requestPath = request.getServletPath();
	        String method = request.getMethod();

	        // Allow authentication APIs without token
	        if (requestPath.startsWith("/stocknest/auth/")) {
	            chain.doFilter(request, response);
	            return;
	        }

	        // If no token is provided
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.getWriter().write("Error: Missing or invalid token");
	            return;
	        }

	        // Extract token
	        String token = authHeader.substring(7);
	        if (!jwtUtil.validateToken(token)) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.getWriter().write("Error: Invalid or expired token");
	            return;
	        }

	        // Extract user details
	        String username = jwtUtil.getUsername(token);
	        String role = jwtUtil.getRole(token);  // Ensure this returns "ROLE_USER" or "ROLE_ADMIN"

	        // Restrict "ROLE_USER" from modifying products and images (only GET is allowed)
	        if (role.equals("ROLE_USER") && RESTRICTED_PATHS.stream().anyMatch(requestPath::startsWith)
	                && !HttpMethod.GET.matches(method)) {
	            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	            response.getWriter().write("Error: Users are only allowed to view products and images and categories");
	            return;
	        }

	        // Set authentication in security context
	        UsernamePasswordAuthenticationToken authentication =
	                new UsernamePasswordAuthenticationToken(
	                        new User(username, "", List.of()), null, List.of());
	        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        chain.doFilter(request, response);
	    }
    
}