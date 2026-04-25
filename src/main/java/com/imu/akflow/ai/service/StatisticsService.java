package com.imu.akflow.ai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imu.akflow.ai.mapper.ConversationMapper;
import com.imu.akflow.ai.mapper.MessageMapper;
import com.imu.akflow.ai.mapper.ModelMapper;
import com.imu.akflow.ai.mapper.ProviderMapper;
import com.imu.akflow.ai.model.entity.Message;
import com.imu.akflow.ai.model.entity.Model;
import com.imu.akflow.ai.model.entity.Provider;
import com.imu.akflow.ai.model.vo.StatisticsOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final MessageMapper messageMapper;
    private final ModelMapper modelMapper;
    private final ProviderMapper providerMapper;
    private final ConversationMapper conversationMapper;

    public StatisticsOverviewVO getOverview(String startDate, String endDate) {
        LocalDateTime start;
        LocalDateTime end;

        if (startDate != null && endDate != null) {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);
        } else {
            start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            end = LocalDateTime.now();
        }

        List<Message> allMessages = messageMapper.selectList(
                Wrappers.lambdaQuery(Message.class)
                        .between(Message::getCreateTime, start, end)
        );

        StatisticsOverviewVO vo = new StatisticsOverviewVO();
        vo.setTotalRequests(allMessages.size());
        vo.setTotalInputTokens(allMessages.stream().mapToInt(m -> m.getInputTokens() != null ? m.getInputTokens() : 0).sum());
        vo.setTotalOutputTokens(allMessages.stream().mapToInt(m -> m.getOutputTokens() != null ? m.getOutputTokens() : 0).sum());
        vo.setTotalCost(allMessages.stream().mapToDouble(m -> m.getCost() != null ? m.getCost() : 0.0).sum());

        Map<Integer, List<Message>> messagesByModel = allMessages.stream()
                .filter(m -> m.getConversationId() != null)
                .collect(Collectors.groupingBy(Message::getConversationId));

        Map<Integer, Integer> modelIdMap = new HashMap<>();
        conversationMapper.selectList(null).forEach(c -> modelIdMap.put(c.getConversationId(), c.getModelId()));

        Map<Integer, List<Message>> groupedByModel = new HashMap<>();
        messagesByModel.forEach((convId, msgs) -> {
            Integer modelId = modelIdMap.get(convId);
            if (modelId != null) {
                groupedByModel.computeIfAbsent(modelId, k -> new ArrayList<>()).addAll(msgs);
            }
        });

        List<StatisticsOverviewVO.ModelStatVO> modelStats = new ArrayList<>();
        groupedByModel.forEach((modelId, msgs) -> {
            Model model = modelMapper.selectById(modelId);
            if (model != null) {
                StatisticsOverviewVO.ModelStatVO stat = new StatisticsOverviewVO.ModelStatVO();
                stat.setModelId(modelId);
                stat.setModelName(model.getDisplayName());
                stat.setRequests(msgs.size());
                stat.setTotalCost(msgs.stream().mapToDouble(m -> m.getCost() != null ? m.getCost() : 0.0).sum());

                Provider provider = providerMapper.selectById(model.getProviderId());
                if (provider != null) {
                    stat.setProviderName(provider.getName());
                }

                modelStats.add(stat);
            }
        });

        modelStats.sort(Comparator.comparing(StatisticsOverviewVO.ModelStatVO::getTotalCost).reversed());
        vo.setModelStats(modelStats);

        return vo;
    }
}
