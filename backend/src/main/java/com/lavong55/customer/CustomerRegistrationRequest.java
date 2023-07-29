package com.lavong55.customer;

public record CustomerRegistrationRequest (
    String name,
    String email,
    Integer age
) {
}
