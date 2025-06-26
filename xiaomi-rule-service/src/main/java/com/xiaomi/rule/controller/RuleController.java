package com.xiaomi.rule.controller;

import com.xiaomi.common.dto.RuleDTO;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.common.result.Result;
import com.xiaomi.rule.service.RuleEngineService;
import com.xiaomi.rule.service.RuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 规则API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/rule")
@RequiredArgsConstructor
@Validated
@Tag(name = "规则管理", description = "预警规则管理API")
public class RuleController {

    private final RuleService ruleService;
    private final RuleEngineService ruleEngineService;

    @PostMapping
    @Operation(summary = "新增规则", description = "新增预警规则")
    public Result<Long> addRule(@Valid @RequestBody RuleDTO ruleDTO) {
        log.info("新增规则请求：{}-{}", ruleDTO.getRuleCode(), ruleDTO.getBatteryType());
        Long id = ruleService.addRule(ruleDTO);
        return Result.success("新增规则成功", id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新规则", description = "根据ID更新规则信息")
    public Result<String> updateRule(
            @Parameter(description = "规则ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody RuleDTO ruleDTO) {
        log.info("更新规则请求：{}", id);
        ruleService.updateRule(id, ruleDTO);
        return Result.success("更新规则成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除规则", description = "根据ID删除规则")
    public Result<String> deleteRule(
            @Parameter(description = "规则ID") @PathVariable @NotNull Long id) {
        log.info("删除规则请求：{}", id);
        ruleService.deleteRule(id);
        return Result.success("删除规则成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询规则", description = "根据规则ID查询规则信息")
    public Result<RuleDTO> getRuleById(
            @Parameter(description = "规则ID") @PathVariable @NotNull Long id) {
        log.info("根据ID查询规则：{}", id);
        RuleDTO ruleDTO = ruleService.getRuleById(id);
        return Result.success("查询成功", ruleDTO);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询规则列表", description = "分页查询规则信息列表")
    public Result<PageResult<RuleDTO>> getRuleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Long size,
            @Parameter(description = "电池类型") @RequestParam(required = false) String batteryType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        log.info("分页查询规则列表，页码：{}，大小：{}，电池类型：{}，状态：{}", current, size, batteryType, status);
        PageResult<RuleDTO> pageResult = ruleService.getRuleList(current, size, batteryType, status);
        return Result.success("查询成功", pageResult);
    }

    @GetMapping("/battery/{batteryType}")
    @Operation(summary = "根据电池类型查询规则", description = "根据电池类型查询启用的规则列表")
    public Result<List<RuleDTO>> getRulesByBatteryType(
            @Parameter(description = "电池类型") @PathVariable @NotBlank String batteryType) {
        log.info("根据电池类型查询规则：{}", batteryType);
        List<RuleDTO> ruleList = ruleService.getEnabledRulesByBatteryType(batteryType);
        return Result.success("查询成功", ruleList);
    }

    @PostMapping("/execute")
    @Operation(summary = "执行规则", description = "根据信号数据执行规则计算预警等级")
    public Result<Integer> executeRule(@RequestBody Map<String, Object> request) {
        String ruleCode = (String) request.get("ruleCode");
        String batteryType = (String) request.get("batteryType");
        @SuppressWarnings("unchecked")
        Map<String, Object> signalData = (Map<String, Object>) request.get("signalData");

        log.info("执行规则请求：{}-{}，信号数据：{}", ruleCode, batteryType, signalData);
        Integer warningLevel = ruleEngineService.executeRule(ruleCode, batteryType, signalData);
        return Result.success("规则执行成功", warningLevel);
    }
}