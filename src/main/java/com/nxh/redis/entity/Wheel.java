package com.nxh.redis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "wheels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wheel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Danh sách các item của vòng quay, lưu dạng JSON string
    @Column(columnDefinition = "TEXT")
    private String itemsJson;

    // Kết quả được đặt trước bởi admin (null = random)
    @Column(name = "preset_result")
    private String presetResult;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
