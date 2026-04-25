package com.imu.akflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.imu.akflow.enums.UploadPathTypeEnum;
import com.imu.akflow.utils.StrBitMapUtil;
import com.imu.akflow.model.entity.base.BaseEntity;
import com.imu.akflow.utils.FileUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("doc")
public class Doc extends BaseEntity {
    @TableId(value = "doc_id", type = IdType.AUTO)
    private Integer docId;

    @TableField(value = "doc_title")
    private String docTitle;

    @TableField(value = "doc_content")
    private String docContent;

    @TableField(value = "upload_path")
    private String uploadPath;

    @TableField(value = "upload_path_type")
    private UploadPathTypeEnum uploadPathType;

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

    public static Doc init(File file, Set<Integer> tagIds) throws Exception {
        Doc doc = new Doc();
        doc.setUploadPath(file.getAbsolutePath());
        doc.setUploadPathType(UploadPathTypeEnum.LOCAL);
        doc.setDocTitle(FileUtil.extractTitleFromFilename(file.getName()));
        doc.setDocContent(FileUtil.readMdFileContent(file));
        doc.setTagCount(tagIds.size());
        doc.setTagBitmap(StrBitMapUtil.toBitmap(tagIds));
        return doc;
    }

}
