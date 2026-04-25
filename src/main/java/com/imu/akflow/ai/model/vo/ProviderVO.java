package com.imu.akflow.ai.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProviderVO {
    private Integer providerId;
    private String name;
    private String baseUrl;
    private String status;
    private LocalDateTime createTime;
}
