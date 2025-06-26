package com.xiaomi.warning.controller;

import com.xiaomi.common.dto.WarningDTO;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.common.result.Result;
import com.xiaomi.warning.service.WarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 预警API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/warning")
@RequiredArgsConstructor
@Validated
@Tag(name = "预警管理", description = "预警信息管理API")
public class WarningController {

    private final WarningService warningService;

    @PostMapping
    @Operation(summary = "新增预警", description = "新增预警信息")
    public Result<Long> addWarning(@Valid @RequestBody WarningDTO warningDTO) {
        log.info("新增预警请求：{}-{}", warningDTO.getVid(), warningDTO.getRuleName());
        Long id = warningService.addWarning(warningDTO);
        return Result.success("新增预警成功", id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新预警", description = "根据ID更新预警信息")
    public Result<String> updateWarning(
            @Parameter(description = "预警ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody WarningDTO warningDTO) {
        log.info("更新预警请求：{}", id);
        warningService.updateWarning(id, warningDTO);
        return Result.success("更新预警成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除预警", description = "根据ID删除预警")
    public Result<String> deleteWarning(
            @Parameter(description = "预警ID") @PathVariable @NotNull Long id) {
        log.info("删除预警请求：{}", id);
        warningService.deleteWarning(id);
        return Result.success("删除预警成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询预警", description = "根据预警ID查询预警信息")
    public Result<WarningDTO> getWarningById(
            @Parameter(description = "预警ID") @PathVariable @NotNull Long id) {
        log.info("根据ID查询预警：{}", id);
        WarningDTO warningDTO = warningService.getWarningById(id);
        return Result.success("查询成功", warningDTO);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询预警列表", description = "分页查询预警信息列表")
    public Result<PageResult<WarningDTO>> getWarningList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Long size,
            @Parameter(description = "车辆VID") @RequestParam(required = false) String vid,
            @Parameter(description = "预警等级") @RequestParam(required = false) Integer warningLevel,
            @Parameter(description = "处理状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("分页查询预警列表，页码：{}，大小：{}，VID：{}，等级：{}，状态：{}",
                current, size, vid, warningLevel, status);
        PageResult<WarningDTO> pageResult = warningService.getWarningList(current, size, vid, warningLevel, status, startTime, endTime);
        return Result.success("查询成功", pageResult);
    }

    @GetMapping("/vid/{vid}")
    @Operation(summary = "根据VID查询预警", description = "根据车辆VID查询预警列表")
    public Result<List<WarningDTO>> getWarningsByVid(
            @Parameter(description = "车辆VID") @PathVariable @NotBlank String vid) {
        log.info("根据VID查询预警：{}", vid);
        List<WarningDTO> warningList = warningService.getWarningsByVid(vid);
        return Result.success("查询成功", warningList);
    }

    @PutMapping("/process/{id}")
    @Operation(summary = "处理预警", description = "标记预警为已处理状态")
    public Result<String> processWarning(
            @Parameter(description = "预警ID") @PathVariable @NotNull Long id) {
        log.info("处理预警请求：{}", id);
        warningService.processWarning(id);
        return Result.success("预警处理成功");
    }

    @GetMapping("/stats")
    @Operation(summary = "预警统计", description = "获取预警统计信息")
    public Result<Map<String, Object>> getWarningStats(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取预警统计信息：{} - {}", startTime, endTime);

        // 默认查询最近7天的数据
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        Map<String, Object> stats = warningService.getWarningStats(startTime, endTime);
        return Result.success("查询成功", stats);
    }
}