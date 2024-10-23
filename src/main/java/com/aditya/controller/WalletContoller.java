package com.aditya.controller;


import com.aditya.model.*;
import com.aditya.service.OrderService;
import com.aditya.service.PaymentService;
import com.aditya.service.UserService;
import com.aditya.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class WalletContoller {
    @Autowired
    private WalletService walleteService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @GetMapping("/api/wallet")
    public ResponseEntity<?> getUserWallet(@RequestHeader("Authorization")String jwt) throws Exception {
        User user=userService.findUserByJwtToken(jwt);

        Wallet wallet = walleteService.getUserWallet(user);

        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }
    @PutMapping("/api/wallet/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(@RequestHeader("Authorization")String jwt,
                                                         @PathVariable Long walletId,
                                                         @RequestBody WalletTransaction req
    ) throws Exception {
        User senderUser =userService.findUserByJwtToken(jwt);


        Wallet reciverWallet = walleteService.findWalletById(walletId);

        Wallet wallet = walleteService.walletToWalletTransfer(senderUser,reciverWallet, req.getAmount());


        return new ResponseEntity<>(wallet,HttpStatus.OK);

    }
    @PutMapping("/api/wallet/order/{orderId}/pay")
    public ResponseEntity<Wallet> PayOrderPayment(@RequestHeader("Authorization")String jwt,
                                                         @PathVariable Long orderId
    ) throws Exception {
        User user =userService.findUserByJwtToken(jwt);
        Order order =orderService.getOrderById(orderId);
        Wallet wallet =walleteService.payOrderPayment(order,user);


        return new ResponseEntity<>(wallet,HttpStatus.OK);

    }
    @PutMapping("/api/wallet/deposite")
    public ResponseEntity<Wallet> addBalanceToWallet(@RequestHeader("Authorization")String jwt,
                                                  @RequestParam(name="order_id") Long orderId,
                                                  @RequestParam(name="payment_id") String paymentId
    ) throws Exception {
        User user =userService.findUserByJwtToken(jwt);

        Wallet wallet =walleteService.getUserWallet(user);
        PaymentOrder order = paymentService.getPaymentOrderById(orderId);
        Boolean status=paymentService.ProccedPaymentOrder(order,paymentId);
        if(wallet.getBalance()==null)
        {
            wallet.setBalance(BigDecimal.valueOf(0));
        }
        if(status){
            wallet=walleteService.addBalanceToWallet(wallet,order.getAmount());

        }

        return new ResponseEntity<>(wallet,HttpStatus.OK);

    }


}
