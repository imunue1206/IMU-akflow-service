package com.imu.akflow.prompt.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocVO {
    private Integer docId;
    private String docTitle;
    private String docContent;
    private String uploadPath;
    private String uploadPathType;
    private Integer tagCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<TagVO> tags;
    private Integer matchCount;
    private Double relevanceScore;
    private Boolean isExactMatch;
}
