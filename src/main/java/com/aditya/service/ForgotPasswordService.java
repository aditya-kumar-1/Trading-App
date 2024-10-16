package com.aditya.service;

import com.aditya.domain.VerificationType;
import com.aditya.model.ForgotPasswordToken;
import com.aditya.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createForgotPasswordToken(User user, String id, String otp, VerificationType verificationType, String sendTo);
    ForgotPasswordToken findById(String id);
    ForgotPasswordToken findByUser(Long userId);
    void deleteToken(ForgotPasswordToken token);

}
