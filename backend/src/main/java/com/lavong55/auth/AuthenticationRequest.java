package com.lavong55.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
