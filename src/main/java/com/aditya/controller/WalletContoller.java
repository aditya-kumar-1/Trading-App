package com.aditya.controller;


import com.aditya.model.Order;
import com.aditya.model.User;
import com.aditya.model.Wallet;
import com.aditya.model.WalletTransaction;
import com.aditya.service.OrderService;
import com.aditya.service.UserService;
import com.aditya.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletContoller {
    @Autowired
    private WalletService walleteService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;
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


}
