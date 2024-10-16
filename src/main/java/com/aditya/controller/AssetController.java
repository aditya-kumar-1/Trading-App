package com.aditya.controller;

import com.aditya.model.Asset;
import com.aditya.model.User;
import com.aditya.service.AssetService;
import com.aditya.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset")
public class AssetController {
    @Autowired
    private final AssetService assetService;
    @Autowired
    private UserService userService;



    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long assetId) throws Exception {
        Asset asset = assetService.getAssetById(assetId);
        return ResponseEntity.ok().body(asset);
    }

    @GetMapping("/coin/{coinId}/user")
    public ResponseEntity<Asset> getAssetByUserIdAndCoinId(
            @PathVariable String coinId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user=userService.findUserByJwtToken(jwt);
        Asset asset = assetService.findAssetByUserIdAndCoinId(user.getId(), coinId);
        return ResponseEntity.ok().body(asset);
    }

    @GetMapping()
    public ResponseEntity<List<Asset>> getAssetsForUser(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        List<Asset> assets = assetService.getUsersAssets(user.getId());
        return ResponseEntity.ok().body(assets);
    }
}
