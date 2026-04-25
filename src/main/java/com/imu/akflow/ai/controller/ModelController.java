package com.imu.akflow.ai.controller;

import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.common.model.Result;
import com.imu.akflow.ai.model.param.ModelParam;
import com.imu.akflow.ai.model.vo.ModelVO;
import com.imu.akflow.ai.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ai/models")
public class ModelController {

    private final ModelService modelService;

    @PostMapping
    public Result<ModelVO> addModel(@RequestBody ModelParam param) {
        return Result.success(modelService.addModel(param));
    }

    @PutMapping("/{modelId}")
    public Result<ModelVO> updateModel(@PathVariable Integer modelId, @RequestBody ModelParam param) {
        modelService.updateModel(modelId, param);
        return Result.success(modelService.getModelById(modelId));
    }

    @DeleteMapping("/{modelId}")
    public Result<Void> deleteModel(@PathVariable Integer modelId) {
        modelService.deleteModel(modelId);
        return Result.success();
    }

    @GetMapping("/{modelId}")
    public Result<ModelVO> getModel(@PathVariable Integer modelId) {
        return Result.success(modelService.getModelById(modelId));
    }

    @GetMapping("/page")
    public Result<PageResult<ModelVO>> pageModels(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer providerId,
            @RequestParam(required = false) String keyword) {
        return Result.success(modelService.pageModels(page, pageSize, providerId, keyword));
    }

    @GetMapping("/list")
    public Result<List<ModelVO>> listModels(@RequestParam(required = false) Integer providerId) {
        if (providerId != null) {
            return Result.success(modelService.listEnabledModels(providerId));
        }
        return Result.success(modelService.listAllEnabledModels());
    }
}
