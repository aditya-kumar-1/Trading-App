package com.aditya.service;

import com.aditya.domain.VerificationType;
import com.aditya.model.User;

public interface UserService {

    public User findUserByEmail(String email) throws Exception;
    public User findUserByJwtToken(String jwtToken) throws Exception;
    public User findUserById(Long  userId) throws Exception;
    public User enableTwoFactorAuthentication( VerificationType verificationType,String sendTo, User user);
    User updatePassword(User user,String newPassword);
}
