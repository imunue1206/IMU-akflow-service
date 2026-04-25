package com.imu.akflow.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imu.akflow.ai.mapper.ModelMapper;
import com.imu.akflow.ai.mapper.ProviderMapper;
import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.ai.model.entity.Model;
import com.imu.akflow.ai.model.entity.Provider;
import com.imu.akflow.ai.model.param.ModelParam;
import com.imu.akflow.ai.model.vo.ModelVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ModelService extends ServiceImpl<ModelMapper, Model> {

    private final ProviderMapper providerMapper;
    private final ObjectMapper objectMapper;

    public ModelVO addModel(ModelParam param) {
        Model model = new Model();
        model.setProviderId(param.getProviderId());
        model.setModelName(param.getModelName());
        model.setDisplayName(param.getDisplayName());
        model.setContextWindow(param.getContextWindow() != null ? param.getContextWindow() : 0);
        model.setPriceInput(param.getPriceInput() != null ? param.getPriceInput() : 0.0);
        model.setPriceOutput(param.getPriceOutput() != null ? param.getPriceOutput() : 0.0);
        model.setCapabilities(toJsonArray(param.getCapabilities()));
        model.setStatus(param.getStatus() != null ? param.getStatus() : "enabled");
        this.save(model);
        return convertToVO(model);
    }

    public void updateModel(Integer modelId, ModelParam param) {
        Model model = this.getById(modelId);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelId);
        }
        if (param.getProviderId() != null) {
            model.setProviderId(param.getProviderId());
        }
        if (StringUtils.isNotBlank(param.getModelName())) {
            model.setModelName(param.getModelName());
        }
        if (StringUtils.isNotBlank(param.getDisplayName())) {
            model.setDisplayName(param.getDisplayName());
        }
        if (param.getContextWindow() != null) {
            model.setContextWindow(param.getContextWindow());
        }
        if (param.getPriceInput() != null) {
            model.setPriceInput(param.getPriceInput());
        }
        if (param.getPriceOutput() != null) {
            model.setPriceOutput(param.getPriceOutput());
        }
        if (param.getCapabilities() != null) {
            model.setCapabilities(toJsonArray(param.getCapabilities()));
        }
        if (StringUtils.isNotBlank(param.getStatus())) {
            model.setStatus(param.getStatus());
        }
        this.updateById(model);
    }

    public void deleteModel(Integer modelId) {
        this.removeById(modelId);
    }

    public ModelVO getModelById(Integer modelId) {
        Model model = this.getById(modelId);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelId);
        }
        return convertToVO(model);
    }

    public PageResult<ModelVO> pageModels(Integer page, Integer pageSize, Integer providerId, String keyword) {
        Page<Model> pageParam = new Page<>(page, pageSize);
        Page<Model> result = getBaseMapper().selectPageWithFilter(pageParam, providerId, keyword);

        return PageResult.of(
                result.getRecords().stream()
                        .map(this::convertToVO)
                        .collect(Collectors.toList()),
                result.getTotal()
        );
    }

    public List<ModelVO> listEnabledModels(Integer providerId) {
        List<Model> models = getBaseMapper().listEnabledByProviderId(providerId);
        return models.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    public List<ModelVO> listAllEnabledModels() {
        List<Model> models = getBaseMapper().listEnabled();
        return models.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    public Model getModelEntityById(Integer modelId) {
        return this.getById(modelId);
    }

    private ModelVO convertToVO(Model model) {
        ModelVO vo = new ModelVO();
        vo.setModelId(model.getModelId());
        vo.setProviderId(model.getProviderId());
        vo.setModelName(model.getModelName());
        vo.setDisplayName(model.getDisplayName());
        vo.setContextWindow(model.getContextWindow());
        vo.setPriceInput(model.getPriceInput());
        vo.setPriceOutput(model.getPriceOutput());
        vo.setCapabilities(parseJsonArray(model.getCapabilities()));
        vo.setStatus(model.getStatus());
        vo.setCreateTime(model.getCreateTime());

        Provider provider = providerMapper.selectById(model.getProviderId());
        if (provider != null) {
            vo.setProviderName(provider.getName());
        }

        return vo;
    }

    private String toJsonArray(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> parseJsonArray(String json) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
