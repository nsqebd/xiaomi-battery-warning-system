package com.xiaomi.vehicle.provider;

import com.xiaomi.common.dto.VehicleDTO;
import com.xiaomi.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 车辆服务Dubbo提供者
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class VehicleProviderImpl implements VehicleProvider {

    private final VehicleService vehicleService;

    @Override
    public VehicleDTO getVehicleByVid(String vid) {
        log.info("Dubbo调用：根据VID查询车辆 - {}", vid);
        return vehicleService.getVehicleByVid(vid);
    }

    @Override
    public VehicleDTO getVehicleByChassisNumber(String chassisNumber) {
        log.info("Dubbo调用：根据车架编号查询车辆 - {}", chassisNumber);
        return vehicleService.getVehicleByChassisNumber(chassisNumber);
    }

    @Override
    public VehicleDTO getVehicleById(Long id) {
        log.info("Dubbo调用：根据ID查询车辆 - {}", id);
        return vehicleService.getVehicleById(id);
    }
}
