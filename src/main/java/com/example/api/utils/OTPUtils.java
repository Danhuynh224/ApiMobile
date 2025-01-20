package com.example.api.utils;

import java.util.Random;

public class OTPUtils {
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
