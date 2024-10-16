package com.aditya.controller;

import com.aditya.model.Coin;
import com.aditya.model.User;
import com.aditya.model.WatchList;
import com.aditya.service.CoinService;
import com.aditya.service.UserService;
import com.aditya.service.WatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchList")
public class WatchListController {
    @Autowired
    private WatchListService watchlistService;
    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;



    @GetMapping("/user")
    public ResponseEntity<WatchList> getUserWatchlist(
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user=userService.findUserByJwtToken(jwt);
        WatchList watchlist = watchlistService.findUserWatchList(user.getId());
        return ResponseEntity.ok(watchlist);

    }



    @GetMapping("/{watchlistId}")
    public ResponseEntity<WatchList> getWatchlistById(
            @PathVariable Long watchlistId) throws Exception {

        WatchList watchlist = watchlistService.findById(watchlistId);
        return ResponseEntity.ok(watchlist);

    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId) throws Exception {


        User user=userService.findUserByJwtToken(jwt);
        Coin coin=coinService.findById(coinId);
        Coin addedCoin = watchlistService.addCoinToWatchList(coin, user);
        return ResponseEntity.ok(addedCoin);

    }
}
