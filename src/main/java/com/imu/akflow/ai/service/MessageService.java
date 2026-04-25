package com.imu.akflow.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imu.akflow.ai.mapper.ConversationMapper;
import com.imu.akflow.ai.mapper.MessageMapper;
import com.imu.akflow.ai.mapper.ModelMapper;
import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.ai.model.entity.Message;
import com.imu.akflow.ai.model.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    private final ConversationMapper conversationMapper;
    private final ModelMapper modelMapper;
    private final ConversationService conversationService;

    public PageResult<MessageVO> pageMessages(Integer conversationId, Integer page, Integer pageSize) {
        Page<Message> pageParam = new Page<>(page, pageSize);
        Page<Message> result = getBaseMapper().selectPageByConversationId(pageParam, conversationId);

        return PageResult.of(
                result.getRecords().stream()
                        .map(this::convertToVO)
                        .collect(Collectors.toList()),
                result.getTotal()
        );
    }

    public List<MessageVO> listMessagesByConversationId(Integer conversationId) {
        List<Message> messages = getBaseMapper().listByConversationId(conversationId);
        return messages.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageVO addAssistantMessage(Integer conversationId, String content, int inputTokens, int outputTokens, double cost) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setRole("assistant");
        message.setContent(content);
        message.setInputTokens(inputTokens);
        message.setOutputTokens(outputTokens);
        message.setCost(cost);
        this.save(message);

        conversationService.updateConversationStats(conversationId, inputTokens, outputTokens, cost);

        return convertToVO(message);
    }

    public MessageVO addUserMessage(Integer conversationId, String content) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setRole("user");
        message.setContent(content);
        message.setInputTokens(0);
        message.setOutputTokens(0);
        message.setCost(0.0);
        this.save(message);

        return convertToVO(message);
    }

    private MessageVO convertToVO(Message message) {
        MessageVO vo = new MessageVO();
        vo.setMessageId(message.getMessageId());
        vo.setConversationId(message.getConversationId());
        vo.setRole(message.getRole());
        vo.setContent(message.getContent());
        vo.setInputTokens(message.getInputTokens());
        vo.setOutputTokens(message.getOutputTokens());
        vo.setCost(message.getCost());
        vo.setCreateTime(message.getCreateTime());
        return vo;
    }
}
