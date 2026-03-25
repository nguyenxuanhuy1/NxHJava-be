package com.nxh.redis.dto;

import lombok.Data;

@Data
public class PresetResultRequest {
    /**
     * Text được gửi vào từ API ngoài.
     * Admin đặt trước kết quả cho lượt quay tiếp theo theo wheelId.
     * Ví dụ: { "result": "huy" }
     */
    private String result;
}
