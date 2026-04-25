package com.imu.akflow.ai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.imu.akflow.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ai_conversation")
public class Conversation extends BaseEntity {
    @TableId(value = "conversation_id", type = IdType.INPUT)
    private Integer conversationId;

    @TableField(value = "model_id")
    private Integer modelId;

    @TableField(value = "title")
    private String title;

    @TableField(value = "status")
    private String status;

    @TableField(value = "total_input_tokens")
    private Integer totalInputTokens;

    @TableField(value = "total_output_tokens")
    private Integer totalOutputTokens;

    @TableField(value = "total_cost")
    private Double totalCost;
}
