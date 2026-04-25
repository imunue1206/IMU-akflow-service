package com.imu.akflow.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imu.akflow.ai.model.entity.Conversation;

import java.util.List;

public interface ConversationMapper extends BaseMapper<Conversation> {

    default Page<Conversation> selectPageWithFilter(Page<Conversation> page, Integer modelId, String status) {
        var query = Wrappers.lambdaQuery(Conversation.class)
                .orderByDesc(Conversation::getUpdateTime);
        if (modelId != null) {
            query.eq(Conversation::getModelId, modelId);
        }
        if (status != null && !status.trim().isEmpty()) {
            query.eq(Conversation::getStatus, status);
        }
        return this.selectPage(page, query);
    }

    default List<Conversation> listByModelId(Integer modelId) {
        return this.selectList(
                Wrappers.lambdaQuery(Conversation.class)
                        .eq(Conversation::getModelId, modelId)
                        .orderByDesc(Conversation::getUpdateTime)
        );
    }
}
