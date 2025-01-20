package com.example.api.controller;

import com.example.api.dtoRequest.ForgetRequest;
import com.example.api.dtoRequest.OTPRequest;
import com.example.api.entity.OTP;
import com.example.api.entity.User;
import com.example.api.repository.OTPRepository;
import com.example.api.repository.UserRepository;
import com.example.api.service.AuthService;
import com.example.api.service.OTPService;
import com.example.api.utils.ErrorResponse;
import com.example.api.utils.OTPUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OTPService otpService;
    @Autowired
    private OTPRepository otpRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "User already exists",
                    "A user with this email already exists in the system.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setActive(false); // Chưa kích hoạt
        userRepository.save(user);
        String otp = OTPUtils.generateOTP();
        otpService.sendOTPToEmail(user.getEmail(), otp);
        return new ResponseEntity<>("Registration successful, OTP sent to email", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null || !BCrypt.checkpw(user.getPassword(), existingUser.getPassword()) || !existingUser.isActive()) {
            ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Login unsuccessful",
                    "Email or password is incorrect.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Login ", HttpStatus.CREATED);
    }

    @PostMapping("/forget")
    public ResponseEntity<?> forget(@RequestBody ForgetRequest forgetRequest) {
        User existingUser = userRepository.findByEmail(forgetRequest.getEmail());

        if (existingUser != null) {
            String newpass = UUID.randomUUID().toString().replace("-", "");
            otpService.sendPassToEmail(existingUser.getEmail(),  newpass );
            existingUser.setPassword(BCrypt.hashpw(newpass, BCrypt.gensalt()));
            userRepository.save(existingUser);
            System.out.println(newpass);
        }
        return new ResponseEntity<>("Please check your email ", HttpStatus.CREATED);
    }

    @PostMapping("/active")
    public ResponseEntity<?> active(@RequestBody OTPRequest otpRequest) {
        OTP existingOTP = otpRepository.findByEmail(otpRequest.getEmail());
        User existingUser = userRepository.findByEmail(otpRequest.getEmail());
        if (existingUser != null && existingOTP != null && !existingUser.isActive()) {
            if (otpRequest.getOtp().equals(existingOTP.getValue())) {
                existingUser.setActive(true);
                userRepository.save(existingUser);
                otpRepository.delete(existingOTP);
                return new ResponseEntity<>("Your account is active", HttpStatus.CREATED);
            }
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Active unsuccessful",
                "Your OTP is incorrect.");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
