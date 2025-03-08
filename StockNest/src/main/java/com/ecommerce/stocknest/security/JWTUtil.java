package com.ecommerce.stocknest.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JWTUtil {

	@Value("${jwt.secret}")
    private String SECRET_KEY;

	private Key key;

    @PostConstruct
    public void init() {
    	
    	if (SECRET_KEY.length() < 32) {
            throw new IllegalArgumentException("JWT Secret key must be at least 32 bytes long");
        }
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
	    
	    
    private static final long EXPIRATION_TIME = 1800000;  // 30 mins in milliseconds
//    private static final long EXPIRATION_TIME = 30000;  // 30 mins in milliseconds

//  private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); //To convert the secret key into a Key object, required for HMAC SHA-256 (HS256) signing.

//    public String generateToken(Authentication authentication) {
//        String roles = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(",")); // Convert roles to comma-separated string
//
//        return Jwts.builder()
//                .setSubject(authentication.getName()) // Set username as subject
//                .claim("roles", roles) // Add roles claim
//                .setIssuedAt(new Date()) // Token issue time
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiry time
//                .signWith(key, SignatureAlgorithm.HS256) // Sign with secret key
//                .compact();
//    }

    
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) // Set username as subject
                .claim("role", role) // Add user role as claim
                .setIssuedAt(new Date()) // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiry time
                .signWith(key, SignatureAlgorithm.HS256) // Signing the token
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
        }
        return false;
    }

    public String getUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String getRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    
}