package com.xiaomi.vehicle.provider;

import com.xiaomi.common.dto.VehicleDTO;

/**
 * 车辆服务Dubbo接口
 */
public interface VehicleProvider {

    /**
     * 根据VID查询车辆
     */
    VehicleDTO getVehicleByVid(String vid);

    /**
     * 根据车架编号查询车辆
     */
    VehicleDTO getVehicleByChassisNumber(String chassisNumber);

    /**
     * 根据ID查询车辆
     */
    VehicleDTO getVehicleById(Long id);
}
