package com.xiaomi.signal.controller;

import com.xiaomi.common.dto.SignalDTO;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.common.result.Result;
import com.xiaomi.signal.service.SignalService;
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

/**
 * 信号API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/signal")
@RequiredArgsConstructor
@Validated
@Tag(name = "信号管理", description = "车辆信号管理API")
public class SignalController {

    private final SignalService signalService;

    @PostMapping
    @Operation(summary = "新增信号", description = "新增车辆信号")
    public Result<Long> addSignal(@Valid @RequestBody SignalDTO signalDTO) {
        log.info("新增信号请求：{}", signalDTO.getVid());
        Long id = signalService.addSignal(signalDTO);
        return Result.success("新增信号成功", id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新信号", description = "根据ID更新信号信息")
    public Result<String> updateSignal(
            @Parameter(description = "信号ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody SignalDTO signalDTO) {
        log.info("更新信号请求：{}", id);
        signalService.updateSignal(id, signalDTO);
        return Result.success("更新信号成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除信号", description = "根据ID删除信号")
    public Result<String> deleteSignal(
            @Parameter(description = "信号ID") @PathVariable @NotNull Long id) {
        log.info("删除信号请求：{}", id);
        signalService.deleteSignal(id);
        return Result.success("删除信号成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询信号", description = "根据信号ID查询信号信息")
    public Result<SignalDTO> getSignalById(
            @Parameter(description = "信号ID") @PathVariable @NotNull Long id) {
        log.info("根据ID查询信号：{}", id);
        SignalDTO signalDTO = signalService.getSignalById(id);
        return Result.success("查询成功", signalDTO);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询信号列表", description = "分页查询信号信息列表")
    public Result<PageResult<SignalDTO>> getSignalList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Long size,
            @Parameter(description = "车辆VID") @RequestParam(required = false) String vid,
            @Parameter(description = "处理状态") @RequestParam(required = false) Integer isProcessed) {
        log.info("分页查询信号列表，页码：{}，大小：{}，VID：{}，处理状态：{}", current, size, vid, isProcessed);
        PageResult<SignalDTO> pageResult = signalService.getSignalList(current, size, vid, isProcessed);
        return Result.success("查询成功", pageResult);
    }

    @GetMapping("/vid/{vid}")
    @Operation(summary = "根据VID查询信号", description = "根据车辆VID查询信号列表")
    public Result<List<SignalDTO>> getSignalsByVid(
            @Parameter(description = "车辆VID") @PathVariable @NotBlank String vid) {
        log.info("根据VID查询信号：{}", vid);
        List<SignalDTO> signalList = signalService.getSignalsByVid(vid);
        return Result.success("查询成功", signalList);
    }

    @PostMapping("/report")
    @Operation(summary = "车辆信号上报", description = "车辆上报信号数据接口")
    public Result<Long> reportSignal(@Valid @RequestBody SignalDTO signalDTO) {
        log.info("车辆信号上报：{}", signalDTO.getVid());
        Long id = signalService.reportSignal(signalDTO);
        return Result.success("信号上报成功", id);
    }

    @GetMapping("/latest/{vid}")
    @Operation(summary = "获取最新信号", description = "获取车辆最新信号数据")
    public Result<SignalDTO> getLatestSignal(
            @Parameter(description = "车辆VID") @PathVariable @NotBlank String vid) {
        log.info("获取最新信号：{}", vid);
        SignalDTO signalDTO = signalService.getLatestSignalByVid(vid);
        return Result.success("查询成功", signalDTO);
    }
}