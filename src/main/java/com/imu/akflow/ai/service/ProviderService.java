package com.imu.akflow.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imu.akflow.ai.mapper.ProviderMapper;
import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.ai.model.entity.Provider;
import com.imu.akflow.ai.model.param.ProviderParam;
import com.imu.akflow.ai.model.vo.ProviderVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProviderService extends ServiceImpl<ProviderMapper, Provider> {

    public ProviderVO addProvider(ProviderParam param) {
        Provider provider = new Provider();
        provider.setName(param.getName());
        provider.setBaseUrl(param.getBaseUrl());
        provider.setApiKey(param.getApiKey());
        provider.setStatus(param.getStatus() != null ? param.getStatus() : "enabled");
        this.save(provider);
        return convertToVO(provider);
    }

    public void updateProvider(Integer providerId, ProviderParam param) {
        Provider provider = this.getById(providerId);
        if (provider == null) {
            throw new IllegalArgumentException("厂商不存在: " + providerId);
        }
        if (StringUtils.isNotBlank(param.getName())) {
            provider.setName(param.getName());
        }
        if (StringUtils.isNotBlank(param.getBaseUrl())) {
            provider.setBaseUrl(param.getBaseUrl());
        }
        if (StringUtils.isNotBlank(param.getApiKey())) {
            provider.setApiKey(param.getApiKey());
        }
        if (StringUtils.isNotBlank(param.getStatus())) {
            provider.setStatus(param.getStatus());
        }
        this.updateById(provider);
    }

    public void deleteProvider(Integer providerId) {
        this.removeById(providerId);
    }

    public ProviderVO getProviderById(Integer providerId) {
        Provider provider = this.getById(providerId);
        if (provider == null) {
            throw new IllegalArgumentException("厂商不存在: " + providerId);
        }
        return convertToVO(provider);
    }

    public PageResult<ProviderVO> pageProviders(Integer page, Integer pageSize, String keyword) {
        Page<Provider> pageParam = new Page<>(page, pageSize);
        Page<Provider> result = getBaseMapper().selectPageWithKeyword(pageParam, keyword);

        return PageResult.of(
                result.getRecords().stream()
                        .map(this::convertToVO)
                        .collect(Collectors.toList()),
                result.getTotal()
        );
    }

    public List<ProviderVO> listEnabledProviders() {
        List<Provider> providers = getBaseMapper().listEnabled();
        return providers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private ProviderVO convertToVO(Provider provider) {
        ProviderVO vo = new ProviderVO();
        vo.setProviderId(provider.getProviderId());
        vo.setName(provider.getName());
        vo.setBaseUrl(provider.getBaseUrl());
        vo.setStatus(provider.getStatus());
        vo.setCreateTime(provider.getCreateTime());
        return vo;
    }
}
