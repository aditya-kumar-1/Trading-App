package com.aditya.service;

import com.aditya.domain.VerificationType;
import com.aditya.model.User;
import com.aditya.model.VerificationCode;
import com.aditya.repository.VerificationCodeRepository;
import com.aditya.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(OtpUtils.generateOtp());
        verificationCode.setVerificationType(verificationType);
        return verificationCodeRepository.save(verificationCode);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {
            Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(id);
            if(verificationCode.isPresent())
            {
                return verificationCode.get();
            }
            throw new Exception("verification code not found");
    }

    @Override
    public VerificationCode getVerificationCodeByUser(Long userId) {
        return verificationCodeRepository.findByUserId(userId);
    }

    @Override
    public void deleteVerificationCodeById(VerificationCode verificationCode) {
    verificationCodeRepository.delete(verificationCode);
    }
}
