package com.aditya.controller;

import com.aditya.config.JwtProvider;
import com.aditya.model.TwoFactorOTP;
import com.aditya.model.User;
import com.aditya.repository.UserRepository;
import com.aditya.response.AuthResponse;
import com.aditya.service.CustomUserDetailsService;
import com.aditya.service.EmailService;
import com.aditya.service.TwoFactorOtpService;
import com.aditya.service.WatchListService;
import com.aditya.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TwoFactorOtpService twoFactorOtpService;
    @Autowired
    private WatchListService watchListService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private EmailService emailService;
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User isEmailExist =userRepository.findByEmail(user.getEmail());
        if(isEmailExist != null){
            throw new Exception("Email is already exists");
        }
        User newUser =new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setFullName(user.getFullName());
        User savedUser = userRepository.save(newUser);
        watchListService.createWatchList(savedUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt= JwtProvider.genrateToken(auth);
        AuthResponse res= new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("Registration success ");
        return new ResponseEntity<>(res, HttpStatus.CREATED);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String userName = user.getEmail();
        String password = user.getPassword();



        Authentication auth = authenticate(userName,password);

        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt= JwtProvider.genrateToken(auth);
        User authUser=userRepository.findByEmail(userName);


        if(user.getTwoFactorAuth().isEnabled())
        {
            AuthResponse res= new AuthResponse();
            res.setMessage("Two Factor Auth is Enabled");
            res.setTwoFactorAuthEnable(true);
            String otp = OtpUtils.generateOtp();
            TwoFactorOTP oldTwoFactorOTP = twoFactorOtpService.findByUser(authUser.getId());
            if(oldTwoFactorOTP!=null)
            {
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }
            TwoFactorOTP newTwoFactorOTP = twoFactorOtpService.createTwoFactorOtp(authUser,otp,jwt);
             emailService.sendVerificationOtpEmail(userName,otp);

            res.setSession(newTwoFactorOTP.getId());
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }
        AuthResponse res= new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("Log in success ");
        return new ResponseEntity<>(res, HttpStatus.CREATED);

    }

    private Authentication authenticate(String userName, String password) {
        UserDetails userDetails =customUserDetailsService.loadUserByUsername(userName);
        if(userDetails == null){
            throw new BadCredentialsException("Invalid username");
        }
        if(!password.equals(userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());
    }
    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse>verifySignOtp(@PathVariable String otp,@RequestParam  String id) throws Exception {
        TwoFactorOTP twoFactorOTP =twoFactorOtpService.findById(id);
        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP,otp))
        {
            AuthResponse res= new AuthResponse();
            res.setMessage("Two Factor Authentication verified");
            res.setTwoFactorAuthEnable(true);
            res.setJwt(twoFactorOTP.getJwt());
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        throw new Exception("invalid otp");
    }
}
