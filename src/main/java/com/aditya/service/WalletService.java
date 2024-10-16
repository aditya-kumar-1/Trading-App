package com.aditya.service;


import com.aditya.model.Order;
import com.aditya.model.User;
import com.aditya.model.Wallet;

public interface WalletService {


    Wallet getUserWallet(User user);

    public Wallet addBalanceToWallet(Wallet wallet, Long money) ;

    public Wallet findWalletById(Long id) throws Exception;

    public Wallet walletToWalletTransfer(User sender,Wallet receiverWallet, Long amount) throws Exception;

    public Wallet payOrderPayment(Order order, User user) throws Exception;



}