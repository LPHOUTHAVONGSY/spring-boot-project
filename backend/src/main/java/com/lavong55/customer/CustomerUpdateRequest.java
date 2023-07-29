package com.lavong55.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
