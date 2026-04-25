package com.imu.akflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadPathTypeEnum {

    LOCAL("00", "本地上传"),
    ;

    private final String code;
    private final String desc;
}
