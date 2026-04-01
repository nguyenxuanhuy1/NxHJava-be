package com.nxh.redis.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO dùng cho danh sách vòng quay (GET /api/wheels).
 * Không bao gồm mảng items để tránh trả dữ liệu nặng (items có thể lên đến 1000 phần tử).
 * Frontend muốn xem items thì gọi GET /api/wheels/{wheelId}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WheelSummaryResponse {
    private Long id;
    private String name;
    @com.fasterxml.jackson.annotation.JsonProperty("preset")
    private String presetResult;   // null nếu lượt sau sẽ random
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
