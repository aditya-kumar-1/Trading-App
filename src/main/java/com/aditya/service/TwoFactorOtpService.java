package com.aditya.service;

import com.aditya.model.TwoFactorOTP;
import com.aditya.model.User;

public interface TwoFactorOtpService {
    TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt);
    TwoFactorOTP findByUser(Long userId);
    TwoFactorOTP findById(String id);
    boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp,String otp);
    void deleteTwoFactorOtp(TwoFactorOTP twoFactorOtp);
}
