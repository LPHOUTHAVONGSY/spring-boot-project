package com.lavong55.auth;

import com.lavong55.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO) {

}
