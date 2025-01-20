package com.example.api.dtoRequest;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class OTPRequest {
    private String email;
    private String otp;


}
