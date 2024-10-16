package com.aditya.repository;

import com.aditya.model.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {
WatchList findByUserId(Long userId);


}
