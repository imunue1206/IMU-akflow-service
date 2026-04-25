package com.imu.akflow.model.param;

import com.imu.akflow.enums.UploadPathTypeEnum;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UploadParam {

    private String path;

    private UploadPathTypeEnum uploadPathType = UploadPathTypeEnum.LOCAL;

    private Set<Integer> tagIds;
}
