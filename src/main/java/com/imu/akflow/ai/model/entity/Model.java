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
@TableName("ai_model")
public class Model extends BaseEntity {
    @TableId(value = "model_id", type = IdType.INPUT)
    private Integer modelId;

    @TableField(value = "provider_id")
    private Integer providerId;

    @TableField(value = "model_name")
    private String modelName;

    @TableField(value = "display_name")
    private String displayName;

    @TableField(value = "context_window")
    private Integer contextWindow;

    @TableField(value = "price_input")
    private Double priceInput;

    @TableField(value = "price_output")
    private Double priceOutput;

    @TableField(value = "capabilities")
    private String capabilities;

    @TableField(value = "status")
    private String status;
}
