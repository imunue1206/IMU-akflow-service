package com.imu.akflow.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imu.akflow.ai.mapper.ConversationMapper;
import com.imu.akflow.ai.mapper.ModelMapper;
import com.imu.akflow.ai.mapper.ProviderMapper;
import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.ai.model.entity.Conversation;
import com.imu.akflow.ai.model.entity.Model;
import com.imu.akflow.ai.model.entity.Provider;
import com.imu.akflow.ai.model.param.ConversationParam;
import com.imu.akflow.ai.model.vo.ConversationVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ConversationService extends ServiceImpl<ConversationMapper, Conversation> {

    private final ModelMapper modelMapper;
    private final ProviderMapper providerMapper;

    public ConversationVO createConversation(ConversationParam param) {
        Model model = modelMapper.selectById(param.getModelId());
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + param.getModelId());
        }
        if (!"enabled".equals(model.getStatus())) {
            throw new IllegalArgumentException("模型已禁用: " + param.getModelId());
        }

        Conversation conversation = new Conversation();
        conversation.setModelId(param.getModelId());
        conversation.setTitle(StringUtils.isNotBlank(param.getTitle()) ? param.getTitle() : "新对话");
        conversation.setStatus("active");
        conversation.setTotalInputTokens(0);
        conversation.setTotalOutputTokens(0);
        conversation.setTotalCost(0.0);
        this.save(conversation);

        return convertToVO(conversation);
    }

    public ConversationVO getConversationById(Integer conversationId) {
        Conversation conversation = this.getById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("对话不存在: " + conversationId);
        }
        return convertToVO(conversation);
    }

    public PageResult<ConversationVO> pageConversations(Integer page, Integer pageSize, Integer modelId, String status) {
        Page<Conversation> pageParam = new Page<>(page, pageSize);
        Page<Conversation> result = getBaseMapper().selectPageWithFilter(pageParam, modelId, status);

        return PageResult.of(
                result.getRecords().stream()
                        .map(this::convertToVO)
                        .collect(Collectors.toList()),
                result.getTotal()
        );
    }

    public void archiveConversation(Integer conversationId) {
        Conversation conversation = this.getById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("对话不存在: " + conversationId);
        }
        conversation.setStatus("archived");
        this.updateById(conversation);
    }

    public void deleteConversation(Integer conversationId) {
        this.removeById(conversationId);
    }

    public void updateConversationStats(Integer conversationId, int inputTokens, int outputTokens, double cost) {
        Conversation conversation = this.getById(conversationId);
        if (conversation == null) {
            return;
        }
        conversation.setTotalInputTokens(conversation.getTotalInputTokens() + inputTokens);
        conversation.setTotalOutputTokens(conversation.getTotalOutputTokens() + outputTokens);
        conversation.setTotalCost(conversation.getTotalCost() + cost);
        this.updateById(conversation);
    }

    private ConversationVO convertToVO(Conversation conversation) {
        ConversationVO vo = new ConversationVO();
        vo.setConversationId(conversation.getConversationId());
        vo.setModelId(conversation.getModelId());
        vo.setTitle(conversation.getTitle());
        vo.setStatus(conversation.getStatus());
        vo.setTotalInputTokens(conversation.getTotalInputTokens());
        vo.setTotalOutputTokens(conversation.getTotalOutputTokens());
        vo.setTotalCost(conversation.getTotalCost());
        vo.setCreateTime(conversation.getCreateTime());
        vo.setUpdateTime(conversation.getUpdateTime());

        Model model = modelMapper.selectById(conversation.getModelId());
        if (model != null) {
            vo.setModelName(model.getDisplayName());
            Provider provider = providerMapper.selectById(model.getProviderId());
            if (provider != null) {
                vo.setProviderName(provider.getName());
            }
        }

        return vo;
    }
}
