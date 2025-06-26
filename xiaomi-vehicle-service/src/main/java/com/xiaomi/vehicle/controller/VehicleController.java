package com.xiaomi.vehicle.controller;

import com.xiaomi.common.dto.VehicleDTO;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.common.result.Result;
import com.xiaomi.vehicle.service.VehicleService;
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

/**
 * 车辆API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/vehicle")
@RequiredArgsConstructor
@Validated
@Tag(name = "车辆管理", description = "车辆信息管理API")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "新增车辆", description = "新增车辆信息")
    public Result<Long> addVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        log.info("新增车辆请求：{}", vehicleDTO.getVid());
        Long id = vehicleService.addVehicle(vehicleDTO);
        return Result.success("新增车辆成功", id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新车辆", description = "根据ID更新车辆信息")
    public Result<String> updateVehicle(
            @Parameter(description = "车辆ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        log.info("更新车辆请求：{}", id);
        vehicleService.updateVehicle(id, vehicleDTO);
        return Result.success("更新车辆成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除车辆", description = "根据ID删除车辆")
    public Result<String> deleteVehicle(
            @Parameter(description = "车辆ID") @PathVariable @NotNull Long id) {
        log.info("删除车辆请求：{}", id);
        vehicleService.deleteVehicle(id);
        return Result.success("删除车辆成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询车辆", description = "根据车辆ID查询车辆信息")
    public Result<VehicleDTO> getVehicleById(
            @Parameter(description = "车辆ID") @PathVariable @NotNull Long id) {
        log.info("根据ID查询车辆：{}", id);
        VehicleDTO vehicleDTO = vehicleService.getVehicleById(id);
        return Result.success("查询成功", vehicleDTO);
    }

    @GetMapping("/vid/{vid}")
    @Operation(summary = "根据VID查询车辆", description = "根据车辆识别码查询车辆信息")
    public Result<VehicleDTO> getVehicleByVid(
            @Parameter(description = "车辆识别码") @PathVariable @NotBlank String vid) {
        log.info("根据VID查询车辆：{}", vid);
        VehicleDTO vehicleDTO = vehicleService.getVehicleByVid(vid);
        return Result.success("查询成功", vehicleDTO);
    }

    @GetMapping("/chassis/{chassisNumber}")
    @Operation(summary = "根据车架编号查询车辆", description = "根据车架编号查询车辆信息")
    public Result<VehicleDTO> getVehicleByChassisNumber(
            @Parameter(description = "车架编号") @PathVariable @NotBlank String chassisNumber) {
        log.info("根据车架编号查询车辆：{}", chassisNumber);
        VehicleDTO vehicleDTO = vehicleService.getVehicleByChassisNumber(chassisNumber);
        return Result.success("查询成功", vehicleDTO);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询车辆列表", description = "分页查询车辆信息列表")
    public Result<PageResult<VehicleDTO>> getVehicleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Long size,
            @Parameter(description = "电池类型") @RequestParam(required = false) String batteryType) {
        log.info("分页查询车辆列表，页码：{}，大小：{}，电池类型：{}", current, size, batteryType);
        PageResult<VehicleDTO> pageResult = vehicleService.getVehicleList(current, size, batteryType);
        return Result.success("查询成功", pageResult);
    }
}