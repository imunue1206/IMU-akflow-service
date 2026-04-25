package com.imu.akflow.ai.controller;

import com.imu.akflow.common.model.Result;
import com.imu.akflow.ai.model.vo.StatisticsOverviewVO;
import com.imu.akflow.ai.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ai/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<StatisticsOverviewVO> getOverview(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(statisticsService.getOverview(startDate, endDate));
    }
}
