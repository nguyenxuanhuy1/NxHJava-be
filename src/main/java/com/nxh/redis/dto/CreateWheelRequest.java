package com.nxh.redis.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateWheelRequest {
    private String name;
    private List<String> items;
}
