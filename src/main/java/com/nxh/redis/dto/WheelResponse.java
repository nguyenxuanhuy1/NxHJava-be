package com.nxh.redis.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WheelResponse {
    private Long id;
    private String name;
    private List<String> items;
    @com.fasterxml.jackson.annotation.JsonProperty("preset")
    private String presetResult;   // null nếu lượt sau sẽ random
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
