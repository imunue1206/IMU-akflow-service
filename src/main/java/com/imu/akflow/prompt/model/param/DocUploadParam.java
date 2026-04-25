package com.imu.akflow.prompt.model.param;

import com.imu.akflow.prompt.enums.UploadPathTypeEnum;
import lombok.Data;

import java.util.Set;

@Data
public class DocUploadParam {

    private String path;

    private UploadPathTypeEnum uploadPathType = UploadPathTypeEnum.LOCAL;

    private Set<Integer> tagIds;
}
