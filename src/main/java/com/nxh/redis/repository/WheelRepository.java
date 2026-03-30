package com.nxh.redis.repository;

import com.nxh.redis.entity.Wheel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WheelRepository extends JpaRepository<Wheel, Long> {

    /**
     * Lấy 10 vòng quay mới nhất (createdAt DESC)
     * Dùng cho GET /api/wheels - không cần phân trang, chỉ lấy 10 cái mới là đủ
     */
    List<Wheel> findTop10ByOrderByCreatedAtDesc();
}
