package com.nxh.redis.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateItemsRequest {
    private List<String> items;
}
