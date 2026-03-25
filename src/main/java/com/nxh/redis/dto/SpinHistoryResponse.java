package com.nxh.redis.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpinHistoryResponse {
    private Long id;
    private Long wheelId;
    private String result;
    private boolean wasPreset;
    private LocalDateTime spunAt;
}
