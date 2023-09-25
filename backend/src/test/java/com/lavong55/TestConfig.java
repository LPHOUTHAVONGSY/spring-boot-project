package com.lavong55;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Annotate this class with @TestConfiguration to indicate that it provides configuration specifically for tests
@TestConfiguration
public class TestConfig {

    // Define a Spring Bean for the PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Create and return a BCryptPasswordEncoder instance
    }
}
