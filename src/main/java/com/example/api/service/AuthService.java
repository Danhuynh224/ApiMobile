package com.example.api.service;

import com.example.api.entity.User;
import com.example.api.repository.UserRepository;
import com.example.api.utils.OTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender emailSender;

    public String registerUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setActive(false);
        userRepository.save(user);
        String otp = OTPUtils.generateOTP();
        sendOTPToEmail(email, otp);
        return otp;  // Return OTP for activation
    }

    public boolean activateUser(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user != null && otp.equals("123456")) {  // OTP check (should be dynamic in production)
            user.setActive(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String otp = OTPUtils.generateOTP();
            sendOTPToEmail(email, otp);
            return otp;  // Return OTP for password reset
        }
        return null;
    }

    public void sendOTPToEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        emailSender.send(message);
    }
}

