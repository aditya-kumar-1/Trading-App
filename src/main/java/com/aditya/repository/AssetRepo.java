package com.aditya.repository;

import com.aditya.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepo extends JpaRepository<Asset, Long> {

    List<Asset> findByUserId(Long userId);
    Asset findByUserIdAndCoinId(Long userId, String coinId);
}
