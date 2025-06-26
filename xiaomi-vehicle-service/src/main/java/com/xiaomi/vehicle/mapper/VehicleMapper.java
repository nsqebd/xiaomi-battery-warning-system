package com.xiaomi.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomi.vehicle.entity.Vehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 车辆Mapper接口
 */
@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {

    /**
     * 根据VID查询车辆
     */
    Vehicle selectByVid(@Param("vid") String vid);

    /**
     * 根据车架编号查询车辆
     */
    Vehicle selectByChassisNumber(@Param("chassisNumber") String chassisNumber);

    /**
     * 分页查询车辆列表
     */
    IPage<Vehicle> selectVehiclePage(Page<Vehicle> page, @Param("batteryType") String batteryType);
}