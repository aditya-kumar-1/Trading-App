package com.aditya.service;

import com.aditya.model.User;
import com.aditya.model.Withdrawal;

import java.util.List;

public interface WithdrawalService {
    Withdrawal requestWithdrawal(Long amount, User user);
    Withdrawal procedWithdrawal(Long withdrawalId,boolean accept) throws Exception;
    List<Withdrawal> getUsersWithdrawalHistory(User user);
    List<Withdrawal> getAllWithdrawalRequest();
}
