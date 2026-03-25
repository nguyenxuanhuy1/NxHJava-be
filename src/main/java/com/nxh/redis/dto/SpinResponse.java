package com.nxh.redis.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpinResponse {
    private Long wheelId;
    private String result;        // Kết quả lượt quay
    private boolean wasPreset;    // true = do admin đặt trước
    private LocalDateTime spunAt;
}
