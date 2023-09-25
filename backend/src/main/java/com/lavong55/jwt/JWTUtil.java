package com.lavong55.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class JWTUtil {

    // Define a secret key used for JWT signing (this should be kept secret in production)
    private static final String SECRET_KEY =
            "foobar_123456789_foobar_123456789_foobar_123456789_foobar_123456789";

    // Method to issue a JWT token with default claims
    public String issueToken(String subject){
        return issueToken(subject, Map.of());
    }

    // Method to issue a JWT token with a variable number of scopes
    public String issueToken(String subject, String ...scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, List<String> scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    // Method to issue a JWT token with custom claims
    public String issueToken(
            String subject,
            Map<String, Object> claims) {

        // Create a JWT token using the builder pattern
        String token = Jwts
                .builder()
                .setClaims(claims) // Set custom claims
                .setSubject(subject) // Set the subject of the token
                .setIssuer("http://lavong55-api.com") // Set the issuer of the token
                .setIssuedAt(Date.from(Instant.now())) // Set the token's issuance date
                .setExpiration(
                        Date.from(
                                Instant.now().plus(15, DAYS) // Set the token's expiration date (15 days from now)
                        )
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign the token with the HMAC-SHA256 algorithm
                .compact(); // Compact the token into a string format

        // Return the generated JWT token
        return token;
    }

    // Method to get the subject (typically, the username) from a JWT token
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    // Private method to parse and retrieve claims from a JWT token
    private Claims getClaims(String token) {
        // Use the JWT parser to build a Claims object from the provided token
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey()) // Set the signing key for token verification
                .build()
                .parseClaimsJws(token) // Parse the token to extract its claims
                .getBody(); // Retrieve the claims (payload) from the parsed token

        // Return the extracted claims from the JWT token
        return claims;
    }

    // Helper method to get the signing key based on the secret key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Method to check if a JWT token is valid for a given username and not expired
    public boolean isTokenValid(String jwt, String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    // Private method to check if a JWT token is expired
    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt).getExpiration().before(today);
    }
}


