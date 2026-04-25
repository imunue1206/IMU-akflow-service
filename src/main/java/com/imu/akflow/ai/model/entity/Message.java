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
@TableName("ai_message")
public class Message extends BaseEntity {
    @TableId(value = "message_id", type = IdType.INPUT)
    private Integer messageId;

    @TableField(value = "conversation_id")
    private Integer conversationId;

    @TableField(value = "role")
    private String role;

    @TableField(value = "content")
    private String content;

    @TableField(value = "input_tokens")
    private Integer inputTokens;

    @TableField(value = "output_tokens")
    private Integer outputTokens;

    @TableField(value = "cost")
    private Double cost;
}
