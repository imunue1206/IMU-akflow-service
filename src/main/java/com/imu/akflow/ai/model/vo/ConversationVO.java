package com.imu.akflow.ai.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationVO {
    private Integer conversationId;
    private Integer modelId;
    private String modelName;
    private String providerName;
    private String title;
    private String status;
    private Integer totalInputTokens;
    private Integer totalOutputTokens;
    private Double totalCost;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
