package com.nxh.redis.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
