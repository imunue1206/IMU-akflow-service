package com.imu.akflow.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagVO {
    private Integer tagId;
    private String tagName;
    private String tagDesc;
    private Integer docCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
