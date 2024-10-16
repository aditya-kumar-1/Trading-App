package com.aditya.service;

import com.aditya.config.JwtProvider;
import com.aditya.domain.VerificationType;
import com.aditya.model.TwoFactorAuth;
import com.aditya.model.User;
import com.aditya.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user==null)
        {
            throw new Exception("user not found");
        }
        return user;
    }

    @Override
    public User findUserByJwtToken(String jwtToken) throws Exception {
        String email= JwtProvider.getEmailFromToken(jwtToken);
        User user = userRepository.findByEmail(email);
        if(user==null)
        {
            throw new Exception("user not found");
        }
        return user;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty())
        {
            throw new Exception("user not found");
        }
        return user.get();
    }

    @Override
    public User enableTwoFactorAuthentication( VerificationType verificationType,String sendTo, User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);
        user.setTwoFactorAuth(twoFactorAuth);
        return userRepository.save(user);
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
