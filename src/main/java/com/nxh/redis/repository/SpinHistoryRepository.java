package com.nxh.redis.repository;

import com.nxh.redis.entity.SpinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpinHistoryRepository extends JpaRepository<SpinHistory, Long> {
    List<SpinHistory> findByWheelIdOrderBySpunAtDesc(Long wheelId);
}
