package com.imu.akflow.model.vo;

import com.imu.akflow.model.entity.Tag;
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

    public static TagVO from(Tag tag) {
        TagVO vo = new TagVO();
        vo.setTagId(tag.getTagId());
        vo.setTagName(tag.getTagName());
        vo.setTagDesc(tag.getTagDesc());
        vo.setDocCount(tag.getDocCount());
        vo.setCreateTime(tag.getCreateTime());
        vo.setUpdateTime(tag.getUpdateTime());
        return vo;
    }
}
