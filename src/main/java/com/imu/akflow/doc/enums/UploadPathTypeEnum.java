package com.imu.akflow.doc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadPathTypeEnum {

    LOCAL("00", "本地上传"),

    CREATE("01", "在线创建"),

    FETCH("02", "远程链接上传")
    ;

    private final String code;
    private final String desc;
}
