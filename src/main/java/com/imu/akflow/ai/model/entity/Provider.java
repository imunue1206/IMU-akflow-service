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
@TableName("ai_provider")
public class Provider extends BaseEntity {
    @TableId(value = "provider_id", type = IdType.INPUT)
    private Integer providerId;

    @TableField(value = "name")
    private String name;

    @TableField(value = "base_url")
    private String baseUrl;

    @TableField(value = "api_key")
    private String apiKey;

    @TableField(value = "status")
    private String status;
}
