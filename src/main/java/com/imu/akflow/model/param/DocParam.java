package com.imu.akflow.model.param;

import lombok.Data;

import java.util.Set;

@Data
public class DocParam {
    private String docTitle;
    private String docContent;
    private String uploadPath;
    private String uploadPathType;
    private Set<Integer> tagIds;
}
