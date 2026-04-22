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
@TableName("doc")
public class Doc extends BaseEntity {
    @TableId(value = "doc_id", type = IdType.AUTO)
    private Long docId;

    @TableField(value = "doc_title")
    private String docTitle;

    @TableField(value = "doc_content")
    private String docContent;

    /**
     * 标签位图，存储tagId（自增id）
     * 示例：如果有标签1,3,5，则位置0,2,4为'1'
     */
    @TableField(value = "tag_bitmap")
    private String tagBitmap;

    /**
     * 标签数量统计（冗余字段，便于查询）
     */
    @TableField(value = "tag_count")
    private Integer tagCount = 0;
}
