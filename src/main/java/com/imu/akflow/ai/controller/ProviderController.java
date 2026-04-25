package com.imu.akflow.ai.controller;

import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.common.model.Result;
import com.imu.akflow.ai.model.param.ProviderParam;
import com.imu.akflow.ai.model.vo.ProviderVO;
import com.imu.akflow.ai.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ai/providers")
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    public Result<ProviderVO> addProvider(@RequestBody ProviderParam param) {
        return Result.success(providerService.addProvider(param));
    }

    @PutMapping("/{providerId}")
    public Result<ProviderVO> updateProvider(@PathVariable Integer providerId, @RequestBody ProviderParam param) {
        providerService.updateProvider(providerId, param);
        return Result.success(providerService.getProviderById(providerId));
    }

    @DeleteMapping("/{providerId}")
    public Result<Void> deleteProvider(@PathVariable Integer providerId) {
        providerService.deleteProvider(providerId);
        return Result.success();
    }

    @GetMapping("/{providerId}")
    public Result<ProviderVO> getProvider(@PathVariable Integer providerId) {
        return Result.success(providerService.getProviderById(providerId));
    }

    @GetMapping("/page")
    public Result<PageResult<ProviderVO>> pageProviders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(providerService.pageProviders(page, pageSize, keyword));
    }

    @GetMapping("/list")
    public Result<List<ProviderVO>> listProviders() {
        return Result.success(providerService.listEnabledProviders());
    }
}
