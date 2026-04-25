package com.imu.akflow.ai.model.param;

import lombok.Data;

import java.util.List;

@Data
public class ModelParam {
    private Integer providerId;
    private String modelName;
    private String displayName;
    private Integer contextWindow;
    private Double priceInput;
    private Double priceOutput;
    private List<String> capabilities;
    private String status;
}
