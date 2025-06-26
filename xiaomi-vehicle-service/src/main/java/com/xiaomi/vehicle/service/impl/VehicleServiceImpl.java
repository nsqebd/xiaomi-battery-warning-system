package com.xiaomi.vehicle.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomi.common.constants.RedisConstants;
import com.xiaomi.common.dto.VehicleDTO;
import com.xiaomi.common.enums.ResponseCodeEnum;
import com.xiaomi.common.exception.BatteryException;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.common.utils.ValidateUtils;
import com.xiaomi.vehicle.entity.Vehicle;
import com.xiaomi.vehicle.mapper.VehicleMapper;
import com.xiaomi.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 车辆服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addVehicle(VehicleDTO vehicleDTO) {
        log.info("新增车辆：{}", vehicleDTO.getVid());

        // 参数校验
        validateVehicleDTO(vehicleDTO);

        // 检查VID是否已存在
        Vehicle existVehicle = vehicleMapper.selectByVid(vehicleDTO.getVid());
        if (existVehicle != null) {
            throw new BatteryException(ResponseCodeEnum.VEHICLE_EXIST.getCode(), "车辆VID已存在：" + vehicleDTO.getVid());
        }

        // 检查车架编号是否已存在
        existVehicle = vehicleMapper.selectByChassisNumber(vehicleDTO.getChassisNumber());
        if (existVehicle != null) {
            throw new BatteryException(ResponseCodeEnum.VEHICLE_EXIST.getCode(), "车架编号已存在：" + vehicleDTO.getChassisNumber());
        }

        // 保存车辆
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(vehicleDTO, vehicle);
        vehicleMapper.insert(vehicle);

        // 更新缓存
        updateVehicleCache(vehicle);

        log.info("新增车辆成功，ID：{}", vehicle.getId());
        return vehicle.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVehicle(Long id, VehicleDTO vehicleDTO) {
        log.info("更新车辆：{}", id);

        // 检查车辆是否存在
        Vehicle existVehicle = vehicleMapper.selectById(id);
        if (existVehicle == null) {
            throw new BatteryException(ResponseCodeEnum.VEHICLE_NOT_FOUND.getCode(), "车辆不存在：" + id);
        }

        // 参数校验
        validateVehicleDTO(vehicleDTO);

        // 如果VID发生变化，检查新VID是否已存在
        if (!existVehicle.getVid().equals(vehicleDTO.getVid())) {
            Vehicle vehicle = vehicleMapper.selectByVid(vehicleDTO.getVid());
            if (vehicle != null && !vehicle.getId().equals(id)) {
                throw new BatteryException(ResponseCodeEnum.VEHICLE_EXIST.getCode(), "车辆VID已存在：" + vehicleDTO.getVid());
            }
        }

        // 如果车架编号发生变化，检查新车架编号是否已存在
        if (!existVehicle.getChassisNumber().equals(vehicleDTO.getChassisNumber())) {
            Vehicle vehicle = vehicleMapper.selectByChassisNumber(vehicleDTO.getChassisNumber());
            if (vehicle != null && !vehicle.getId().equals(id)) {
                throw new BatteryException(ResponseCodeEnum.VEHICLE_EXIST.getCode(), "车架编号已存在：" + vehicleDTO.getChassisNumber());
            }
        }

        // 更新车辆
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(vehicleDTO, vehicle);
        vehicle.setId(id);
        vehicleMapper.updateById(vehicle);

        // 删除旧缓存
        clearVehicleCache(existVehicle);

        // 更新新缓存
        Vehicle updatedVehicle = vehicleMapper.selectById(id);
        updateVehicleCache(updatedVehicle);

        log.info("更新车辆成功：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVehicle(Long id) {
        log.info("删除车辆：{}", id);

        // 检查车辆是否存在
        Vehicle existVehicle = vehicleMapper.selectById(id);
        if (existVehicle == null) {
            throw new BatteryException(ResponseCodeEnum.VEHICLE_NOT_FOUND.getCode(), "车辆不存在：" + id);
        }

        // 逻辑删除
        vehicleMapper.deleteById(id);

        // 删除缓存
        clearVehicleCache(existVehicle);

        log.info("删除车辆成功：{}", id);
    }

    @Override
    public VehicleDTO getVehicleById(Long id) {
        log.info("根据ID查询车辆：{}", id);

        // 先从缓存查询
        String cacheKey = RedisConstants.CACHE_VEHICLE_INFO + id;
        VehicleDTO cachedVehicle = (VehicleDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedVehicle != null) {
            log.info("从缓存获取车辆信息：{}", id);
            return cachedVehicle;
        }

        // 从数据库查询
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BatteryException(ResponseCodeEnum.VEHICLE_NOT_FOUND.getCode(), "车辆不存在：" + id);
        }

        VehicleDTO vehicleDTO = new VehicleDTO();
        BeanUtils.copyProperties(vehicle, vehicleDTO);

        // 缓存车辆信息
        redisTemplate.opsForValue().set(cacheKey, vehicleDTO, RedisConstants.CACHE_EXPIRE_VEHICLE, TimeUnit.SECONDS);

        return vehicleDTO;
    }

    @Override
    public VehicleDTO getVehicleByVid(String vid) {
        log.info("根据VID查询车辆：{}", vid);

        ValidateUtils.requireNonBlank(vid, "VID不能为空");

        // 先从缓存查询
        String cacheKey = RedisConstants.CACHE_VEHICLE_VID + vid;
        VehicleDTO cachedVehicle = (VehicleDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedVehicle != null) {
            log.info("从缓存获取车辆信息：{}", vid);
            return cachedVehicle;
        }

        // 从数据库查询
        Vehicle vehicle = vehicleMapper.selectByVid(vid);
        if (vehicle == null) {
            throw new BatteryException(ResponseCodeEnum.VEHICLE_NOT_FOUND.getCode(), "车辆不存在：" + vid);
        }

        VehicleDTO vehicleDTO = new VehicleDTO();
        BeanUtils.copyProperties(vehicle, vehicleDTO);

        // 缓存车辆信息
        redisTemplate.opsForValue().set(cacheKey, vehicleDTO, RedisConstants.CACHE_EXPIRE_VEHICLE, TimeUnit.SECONDS);

        return vehicleDTO;
    }

    @Override
    public VehicleDTO getVehicleByChassisNumber(String chassisNumber) {
        log.info("根据车架编号查询车辆：{}", chassisNumber);

        ValidateUtils.requireNonBlank(chassisNumber, "车架编号不能为空");

        // 先从缓存查询
        String cacheKey = RedisConstants.CACHE_VEHICLE_CHASSIS + chassisNumber;
        VehicleDTO cachedVehicle = (VehicleDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedVehicle != null) {
            log.info("从缓存获取车辆信息：{}", chassisNumber);
            return cachedVehicle;
        }

        // 从数据库查询
        Vehicle vehicle = vehicleMapper.selectByChassisNumber(chassisNumber);
        if (vehicle == null) {
            throw new BatteryException(ResponseCodeEnum.VEHICLE_NOT_FOUND.getCode(), "车辆不存在：" + chassisNumber);
        }

        VehicleDTO vehicleDTO = new VehicleDTO();
        BeanUtils.copyProperties(vehicle, vehicleDTO);

        // 缓存车辆信息
        redisTemplate.opsForValue().set(cacheKey, vehicleDTO, RedisConstants.CACHE_EXPIRE_VEHICLE, TimeUnit.SECONDS);

        return vehicleDTO;
    }

    @Override
    public PageResult<VehicleDTO> getVehicleList(Long current, Long size, String batteryType) {
        log.info("分页查询车辆列表，页码：{}，大小：{}，电池类型：{}", current, size, batteryType);

        Page<Vehicle> page = new Page<>(current, size);
        IPage<Vehicle> vehiclePage = vehicleMapper.selectVehiclePage(page, batteryType);

        List<VehicleDTO> vehicleDTOList = vehiclePage.getRecords().stream()
                .map(vehicle -> {
                    VehicleDTO vehicleDTO = new VehicleDTO();
                    BeanUtils.copyProperties(vehicle, vehicleDTO);
                    return vehicleDTO;
                })
                .collect(Collectors.toList());

        return PageResult.of(vehicleDTOList, vehiclePage.getTotal(), current, size);
    }

    /**
     * 校验车辆DTO
     */
    private void validateVehicleDTO(VehicleDTO vehicleDTO) {
        ValidateUtils.requireNonBlank(vehicleDTO.getVid(), "VID不能为空");
        ValidateUtils.requireNonBlank(vehicleDTO.getChassisNumber(), "车架编号不能为空");
        ValidateUtils.requireNonBlank(vehicleDTO.getBatteryType(), "电池类型不能为空");

        if (!ValidateUtils.isValidVid(vehicleDTO.getVid())) {
            throw new BatteryException(ResponseCodeEnum.PARAM_ERROR.getCode(), "VID格式错误，必须是16位字母数字组合");
        }

        if (!ValidateUtils.isValidBatteryType(vehicleDTO.getBatteryType())) {
            throw new BatteryException(ResponseCodeEnum.PARAM_ERROR.getCode(), "电池类型无效，只能是：三元电池、铁锂电池");
        }

        if (vehicleDTO.getBatteryHealth() != null && !ValidateUtils.isValidBatteryHealth(vehicleDTO.getBatteryHealth())) {
            throw new BatteryException(ResponseCodeEnum.PARAM_ERROR.getCode(), "电池健康状态必须在0-100之间");
        }
    }

    /**
     * 更新车辆缓存
     */
    private void updateVehicleCache(Vehicle vehicle) {
        VehicleDTO vehicleDTO = new VehicleDTO();
        BeanUtils.copyProperties(vehicle, vehicleDTO);

        // 缓存多个维度的数据
        redisTemplate.opsForValue().set(RedisConstants.CACHE_VEHICLE_INFO + vehicle.getId(),
                vehicleDTO, RedisConstants.CACHE_EXPIRE_VEHICLE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(RedisConstants.CACHE_VEHICLE_VID + vehicle.getVid(),
                vehicleDTO, RedisConstants.CACHE_EXPIRE_VEHICLE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(RedisConstants.CACHE_VEHICLE_CHASSIS + vehicle.getChassisNumber(),
                vehicleDTO, RedisConstants.CACHE_EXPIRE_VEHICLE, TimeUnit.SECONDS);
    }

    /**
     * 清除车辆缓存
     */
    private void clearVehicleCache(Vehicle vehicle) {
        redisTemplate.delete(RedisConstants.CACHE_VEHICLE_INFO + vehicle.getId());
        redisTemplate.delete(RedisConstants.CACHE_VEHICLE_VID + vehicle.getVid());
        redisTemplate.delete(RedisConstants.CACHE_VEHICLE_CHASSIS + vehicle.getChassisNumber());
    }
}