package com.imu.akflow.ai.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {
    private Integer messageId;
    private Integer conversationId;
    private String role;
    private String content;
    private Integer inputTokens;
    private Integer outputTokens;
    private Double cost;
    private LocalDateTime createTime;
}
