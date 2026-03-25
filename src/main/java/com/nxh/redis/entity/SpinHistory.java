package com.nxh.redis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "spin_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpinHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wheel_id", nullable = false)
    private Long wheelId;

    @Column(name = "result", nullable = false)
    private String result;

    // true = kết quả do admin preset, false = random
    @Column(name = "was_preset")
    private boolean wasPreset;

    @Column(name = "spun_at")
    private LocalDateTime spunAt;

    @PrePersist
    public void prePersist() {
        this.spunAt = LocalDateTime.now();
    }
}
