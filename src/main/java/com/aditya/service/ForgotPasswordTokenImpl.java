package com.aditya.service;

import com.aditya.domain.VerificationType;
import com.aditya.model.ForgotPasswordToken;
import com.aditya.model.User;
import com.aditya.repository.ForgotPasswordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordTokenImpl implements ForgotPasswordService{

    @Autowired
    private ForgotPasswordRepo forgotPasswordRepo;

    @Override
    public ForgotPasswordToken createForgotPasswordToken(User user, String id, String otp, VerificationType verificationType, String sendTo) {
        ForgotPasswordToken token =new ForgotPasswordToken();
        token.setUser(user);
        token.setSendTo(sendTo);
        token.setVerificationType(verificationType);
        token.setOtp(otp);
        token.setId(id);


        return forgotPasswordRepo.save(token);
    }

    @Override
    public ForgotPasswordToken findById(String id) {
        Optional<ForgotPasswordToken> token =forgotPasswordRepo.findById(id);
        return token.orElse(null);
    }

    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return forgotPasswordRepo.findByUserId(userId);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
    forgotPasswordRepo.delete(token);
    }
}
