package com.imu.akflow.prompt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imu.akflow.prompt.model.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    int updateBatchById(@Param("list") List<Tag> list);
}
