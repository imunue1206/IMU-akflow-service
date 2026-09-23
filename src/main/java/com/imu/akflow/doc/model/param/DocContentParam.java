package com.imu.akflow.doc.model.param;

import lombok.Data;

import java.util.Set;

@Data
public class DocContentParam {

    private String docTitle;

    private String docContent;

    private Set<Integer> tagIds;
}