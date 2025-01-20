package com.example.api.controller;

import com.example.api.entity.User;
import com.example.api.repository.UserRepository;
import com.example.api.service.AuthService;
import com.example.api.utils.ErrorResponse;
import com.example.api.utils.OTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "User already exists",
                    "A user with this email already exists in the system.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        user.setActive(false); // Chưa kích hoạt
        userRepository.save(user);
        String otp = OTPUtils.generateOTP();
        authService.sendOTPToEmail(user.getEmail(), otp);
        return new ResponseEntity<>("Registration successful, OTP sent to email", HttpStatus.CREATED);
    }


}
