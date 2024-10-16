package com.aditya.service;

import com.aditya.model.Coin;
import com.aditya.model.User;
import com.aditya.model.WatchList;

public interface WatchListService {
    WatchList findUserWatchList(Long userId) throws Exception;
    WatchList createWatchList(User user);
    WatchList findById(Long id) throws Exception;
    Coin addCoinToWatchList(Coin coin, User user) throws Exception;
}
