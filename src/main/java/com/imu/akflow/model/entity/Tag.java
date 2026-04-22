package com.imu.akflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.imu.akflow.model.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("tag")
public class Tag extends BaseEntity {
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;

    @TableField(value = "tag_name")
    private String tagName;

    @TableField(value = "tag_desc")
    private String tagDesc;

    /**
     * 文档位图，存储docId（自增id）
     * 示例：如果有文档1,3,5，则位置0,2,4为'1'
     */
    @TableField(value = "doc_bitmap")
    private String docBitmap;

    /**
     * 文档数量统计（冗余字段，便于查询）
     */
    @TableField(value = "doc_count")
    private Integer docCount = 0;
}
