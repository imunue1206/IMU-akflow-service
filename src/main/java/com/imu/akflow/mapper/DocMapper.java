package com.imu.akflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imu.akflow.model.entity.Doc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DocMapper extends BaseMapper<Doc> {

    default Doc queryByDocTitle(String docTitle) {
        return this.selectOne(
                Wrappers.lambdaQuery(Doc.class)
                        .eq(Doc::getDocTitle, docTitle)
        );
    }

    int updateBatchById(@Param("list") List<Doc> list);
}
