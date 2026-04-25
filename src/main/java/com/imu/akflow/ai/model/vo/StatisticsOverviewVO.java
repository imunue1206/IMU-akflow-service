package com.imu.akflow.ai.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsOverviewVO {
    private Integer totalRequests;
    private Integer totalInputTokens;
    private Integer totalOutputTokens;
    private Double totalCost;
    private List<ModelStatVO> modelStats;

    @Data
    public static class ModelStatVO {
        private Integer modelId;
        private String modelName;
        private String providerName;
        private Integer requests;
        private Double totalCost;
    }
}
