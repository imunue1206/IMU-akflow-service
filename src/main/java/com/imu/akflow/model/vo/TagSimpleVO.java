package com.imu.akflow.model.vo;

import com.imu.akflow.model.entity.Tag;
import lombok.Data;

@Data
public class TagSimpleVO {
    private Integer tagId;
    private String tagName;
    private String tagDesc;

    public static TagSimpleVO from(Tag tag) {
        TagSimpleVO vo = new TagSimpleVO();
        vo.setTagId(tag.getTagId());
        vo.setTagName(tag.getTagName());
        vo.setTagDesc(tag.getTagDesc());
        return vo;
    }
}
