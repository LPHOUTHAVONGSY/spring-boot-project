package com.lavong55.jwt;

import com.lavong55.customer.CustomerUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

// Annotate this class with @Component to mark it as a Spring Bean component
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    // Declare private fields for JWTUtil and UserDetailsService
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // Create a constructor that injects JWTUtil and CustomerUserDetailsService instances
    public JWTAuthenticationFilter(JWTUtil jwtUtil,
                                   CustomerUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Override the doFilterInternal method from OncePerRequestFilter
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the "Authorization" header from the incoming HTTP request
        String authHeader = request.getHeader("Authorization");

        // Check if the "Authorization" header is missing or does not start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If either condition is true, continue with the filter chain.
            // This means that the header is either missing or not in the expected "Bearer <token>" format,
            // which is commonly used for JWT (JSON Web Token) authentication.
            // In such cases, JWT authentication is not attempted.
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT (token) from the Authorization header (remove "Bearer ")
        String jwt = authHeader.substring(7);

        // Extract the subject (typically, the username) from the JWT
        String subject = jwtUtil.getSubject(jwt);

        // Check if there is no authentication in the current security context
        if (subject != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details by the subject (username) extracted from the JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            // Check if the JWT is valid for the user
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {

                // Create an authentication token using the user details and no credentials
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                // Set authentication token details using the request details
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set the authentication token in the security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
