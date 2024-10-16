package com.aditya.service;

import com.aditya.domain.VerificationType;
import com.aditya.model.User;
import com.aditya.model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);
    VerificationCode getVerificationCodeById(Long Id) throws Exception;
    VerificationCode getVerificationCodeByUser(Long userId);

    void deleteVerificationCodeById(VerificationCode verificationCode);
}
