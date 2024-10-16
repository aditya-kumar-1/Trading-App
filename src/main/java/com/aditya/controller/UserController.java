package com.aditya.controller;

import com.aditya.request.ForgotPasswordTokenRequest;
import com.aditya.domain.VerificationType;
import com.aditya.model.ForgotPasswordToken;
import com.aditya.model.User;
import com.aditya.model.VerificationCode;
import com.aditya.request.ResetPasswordRequest;
import com.aditya.response.ApiResponse;
import com.aditya.response.AuthResponse;
import com.aditya.service.EmailService;
import com.aditya.service.ForgotPasswordService;
import com.aditya.service.UserService;
import com.aditya.service.VerificationCodeService;
import com.aditya.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {
@Autowired
    private UserService userService;
@Autowired
private VerificationCodeService verificationCodeService;
@Autowired
    private EmailService emailService;
@Autowired
private ForgotPasswordService forgotPasswordService;
private String jwt;
@GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authenticator") String jwt) throws Exception {
    User user =userService.findUserByJwtToken(jwt);
    return new ResponseEntity<User>(user, HttpStatus.OK);
}

@PostMapping("/api/users/verification/{verificationType}/send-otp")
public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt,
                                                    @PathVariable VerificationType verificationType) throws Exception {


        User user =userService.findUserByJwtToken(jwt);
    VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
    if(verificationCode == null) {
        verificationCodeService.sendVerificationCode(user, verificationType);
    }
    if(verificationType.equals(VerificationType.EMAIL)) {
        emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());

    }


    return new ResponseEntity<String>("verifification otp send successfully",HttpStatus.OK);


    }
@PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
public ResponseEntity<User> enableTwoFactorAuthentication(@PathVariable String otp ,@RequestHeader("Authorization") String jwt) throws Exception {
    User user = userService.findUserByJwtToken(jwt);
    VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
    String sendTo=verificationCode.getVerificationType().equals(VerificationType.EMAIL)? verificationCode.getEmail() : verificationCode.getOtp();


    boolean isVerified=verificationCode.getOtp().equals(otp);
    if(isVerified) {

        User updatedUser= userService.enableTwoFactorAuthentication(
                verificationCode.getVerificationType(),sendTo,user);
        verificationCodeService.deleteVerificationCodeById(verificationCode);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
    throw new Exception("wrong otp");

}
    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
                                                        @RequestBody ForgotPasswordTokenRequest req) throws Exception {


        User user = userService.findUserByEmail(req.getSendTo());
        String otp= OtpUtils.generateOtp();
        UUID uuid= UUID.randomUUID();
        String id= uuid.toString();
        ForgotPasswordToken token =forgotPasswordService.findByUser(user.getId());
        if(token == null) {

            token=forgotPasswordService.createForgotPasswordToken(user,id,otp,req.getVerificationType(), req.getSendTo());
        }

        if(req.getVerificationType().equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(
                    user.getEmail(),
                    token.getOtp());
        }
        AuthResponse response =new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("password reset otp send successfully");

        return new ResponseEntity<>(response,HttpStatus.OK);


    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword( @RequestParam String id,
                                               @RequestBody ResetPasswordRequest req,
                                               @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);


        boolean isVerified=forgotPasswordToken.getOtp().equals(req.getOtp());
        if(isVerified) {

           userService.updatePassword(forgotPasswordToken.getUser(),req.getPassword());
            ApiResponse res = new ApiResponse();
            res.setMessage("password reset otp successfully");
            return new ResponseEntity<>(res,HttpStatus.OK);

        }
        throw new Exception("wrong otp");

    }



}
