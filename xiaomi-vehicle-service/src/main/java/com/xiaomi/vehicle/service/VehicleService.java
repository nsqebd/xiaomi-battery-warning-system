package com.xiaomi.vehicle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomi.common.dto.VehicleDTO;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.vehicle.entity.Vehicle;

/**
 * 车辆服务接口
 */
public interface VehicleService extends IService<Vehicle> {

    /**
     * 新增车辆
     */
    Long addVehicle(VehicleDTO vehicleDTO);

    /**
     * 更新车辆
     */
    void updateVehicle(Long id, VehicleDTO vehicleDTO);

    /**
     * 删除车辆
     */
    void deleteVehicle(Long id);

    /**
     * 根据ID查询车辆
     */
    VehicleDTO getVehicleById(Long id);

    /**
     * 根据VID查询车辆
     */
    VehicleDTO getVehicleByVid(String vid);

    /**
     * 根据车架编号查询车辆
     */
    VehicleDTO getVehicleByChassisNumber(String chassisNumber);

    /**
     * 分页查询车辆列表
     */
    PageResult<VehicleDTO> getVehicleList(Long current, Long size, String batteryType);
}