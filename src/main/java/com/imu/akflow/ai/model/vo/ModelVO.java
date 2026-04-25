package com.imu.akflow.ai.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ModelVO {
    private Integer modelId;
    private Integer providerId;
    private String providerName;
    private String modelName;
    private String displayName;
    private Integer contextWindow;
    private Double priceInput;
    private Double priceOutput;
    private List<String> capabilities;
    private String status;
    private LocalDateTime createTime;
}
