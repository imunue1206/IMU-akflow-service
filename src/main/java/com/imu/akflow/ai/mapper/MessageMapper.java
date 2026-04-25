package com.imu.akflow.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imu.akflow.ai.model.entity.Message;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {

    default Page<Message> selectPageByConversationId(Page<Message> page, Integer conversationId) {
        return this.selectPage(page,
                Wrappers.lambdaQuery(Message.class)
                        .eq(Message::getConversationId, conversationId)
                        .orderByAsc(Message::getCreateTime)
        );
    }

    default List<Message> listByConversationId(Integer conversationId) {
        return this.selectList(
                Wrappers.lambdaQuery(Message.class)
                        .eq(Message::getConversationId, conversationId)
                        .orderByAsc(Message::getCreateTime)
        );
    }
}
