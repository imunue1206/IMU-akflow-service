package com.imu.akflow.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imu.akflow.ai.model.entity.Provider;

import java.util.List;

public interface ProviderMapper extends BaseMapper<Provider> {

    default Page<Provider> selectPageWithKeyword(Page<Provider> page, String keyword) {
        var query = Wrappers.lambdaQuery(Provider.class)
                .orderByDesc(Provider::getCreateTime);
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.like(Provider::getName, keyword);
        }
        return this.selectPage(page, query);
    }

    default List<Provider> listEnabled() {
        return this.selectList(
                Wrappers.lambdaQuery(Provider.class)
                        .eq(Provider::getStatus, "enabled")
                        .orderByDesc(Provider::getCreateTime)
        );
    }
}
