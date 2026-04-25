package com.imu.akflow.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imu.akflow.ai.model.entity.Model;

import java.util.List;

public interface ModelMapper extends BaseMapper<Model> {

    default Page<Model> selectPageWithFilter(Page<Model> page, Integer providerId, String keyword) {
        var query = Wrappers.lambdaQuery(Model.class)
                .orderByDesc(Model::getCreateTime);
        if (providerId != null) {
            query.eq(Model::getProviderId, providerId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.like(Model::getModelName, keyword).or().like(Model::getDisplayName, keyword);
        }
        return this.selectPage(page, query);
    }

    default List<Model> listEnabledByProviderId(Integer providerId) {
        var query = Wrappers.lambdaQuery(Model.class)
                .eq(Model::getStatus, "enabled");
        if (providerId != null) {
            query.eq(Model::getProviderId, providerId);
        }
        return this.selectList(query);
    }

    default List<Model> listEnabled() {
        return this.selectList(
                Wrappers.lambdaQuery(Model.class)
                        .eq(Model::getStatus, "enabled")
                        .orderByDesc(Model::getCreateTime)
        );
    }
}
