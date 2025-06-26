package com.xiaomi.signal.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomi.common.constants.BatteryConstants;
import com.xiaomi.common.constants.RedisConstants;
import com.xiaomi.common.dto.SignalDTO;
import com.xiaomi.common.dto.VehicleDTO;
import com.xiaomi.common.enums.ResponseCodeEnum;
import com.xiaomi.common.exception.BatteryException;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.common.utils.JsonUtils;
import com.xiaomi.common.utils.ValidateUtils;
import com.xiaomi.signal.entity.Signal;
import com.xiaomi.signal.mapper.SignalMapper;
import com.xiaomi.signal.service.SignalService;
import com.xiaomi.vehicle.provider.VehicleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 信号服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignalServiceImpl extends ServiceImpl<SignalMapper, Signal> implements SignalService {

    private final SignalMapper signalMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @DubboReference(version = "1.0.0", check = false)
    private VehicleProvider vehicleProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSignal(SignalDTO signalDTO) {
        log.info("新增信号：{}", signalDTO.getVid());

        // 参数校验
        validateSignalDTO(signalDTO);

        // 验证车辆是否存在
        validateVehicleExists(signalDTO.getVid(), signalDTO.getChassisNumber());

        // 保存信号
        Signal signal = new Signal();
        BeanUtils.copyProperties(signalDTO, signal);
        parseSignalData(signal);
        signalMapper.insert(signal);

        // 更新最新信号缓存
        updateLatestSignalCache(signal);

        log.info("新增信号成功，ID：{}", signal.getId());
        return signal.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSignal(Long id, SignalDTO signalDTO) {
        log.info("更新信号：{}", id);

        // 检查信号是否存在
        Signal existSignal = signalMapper.selectById(id);
        if (existSignal == null) {
            throw new BatteryException(ResponseCodeEnum.DATA_NOT_FOUND.getCode(), "信号不存在：" + id);
        }

        // 参数校验
        validateSignalDTO(signalDTO);

        // 验证车辆是否存在
        validateVehicleExists(signalDTO.getVid(), signalDTO.getChassisNumber());

        // 更新信号
        Signal signal = new Signal();
        BeanUtils.copyProperties(signalDTO, signal);
        signal.setId(id);
        parseSignalData(signal);
        signalMapper.updateById(signal);

        // 如果是最新信号，更新缓存
        if (existSignal.getVid().equals(signalDTO.getVid())) {
            Signal latestSignal = signalMapper.selectLatestByVid(signalDTO.getVid());
            if (latestSignal != null && latestSignal.getId().equals(id)) {
                updateLatestSignalCache(latestSignal);
            }
        }

        log.info("更新信号成功：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSignal(Long id) {
        log.info("删除信号：{}", id);

        // 检查信号是否存在
        Signal existSignal = signalMapper.selectById(id);
        if (existSignal == null) {
            throw new BatteryException(ResponseCodeEnum.DATA_NOT_FOUND.getCode(), "信号不存在：" + id);
        }

        // 物理删除
        signalMapper.deleteById(id);

        // 如果删除的是最新信号，更新缓存
        Signal latestSignal = signalMapper.selectLatestByVid(existSignal.getVid());
        if (latestSignal != null) {
            updateLatestSignalCache(latestSignal);
        } else {
            // 如果没有其他信号，删除缓存
            redisTemplate.delete(RedisConstants.CACHE_SIGNAL_LATEST + existSignal.getVid());
        }

        log.info("删除信号成功：{}", id);
    }

    @Override
    public SignalDTO getSignalById(Long id) {
        log.info("根据ID查询信号：{}", id);

        Signal signal = signalMapper.selectById(id);
        if (signal == null) {
            throw new BatteryException(ResponseCodeEnum.DATA_NOT_FOUND.getCode(), "信号不存在：" + id);
        }

        SignalDTO signalDTO = new SignalDTO();
        BeanUtils.copyProperties(signal, signalDTO);

        return signalDTO;
    }

    @Override
    public PageResult<SignalDTO> getSignalList(Long current, Long size, String vid, Integer isProcessed) {
        log.info("分页查询信号列表，页码：{}，大小：{}，VID：{}，处理状态：{}", current, size, vid, isProcessed);

        Page<Signal> page = new Page<>(current, size);
        IPage<Signal> signalPage = signalMapper.selectSignalPage(page, vid, isProcessed);

        List<SignalDTO> signalDTOList = signalPage.getRecords().stream()
                .map(signal -> {
                    SignalDTO signalDTO = new SignalDTO();
                    BeanUtils.copyProperties(signal, signalDTO);
                    return signalDTO;
                })
                .collect(Collectors.toList());

        return PageResult.of(signalDTOList, signalPage.getTotal(), current, size);
    }

    @Override
    public List<SignalDTO> getSignalsByVid(String vid) {
        log.info("根据VID查询信号列表：{}", vid);

        ValidateUtils.requireNonBlank(vid, "VID不能为空");

        List<Signal> signalList = signalMapper.selectByVid(vid);
        return signalList.stream()
                .map(signal -> {
                    SignalDTO signalDTO = new SignalDTO();
                    BeanUtils.copyProperties(signal, signalDTO);
                    return signalDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long reportSignal(SignalDTO signalDTO) {
        log.info("车辆信号上报：{}", signalDTO.getVid());
        // 参数校验
        validateSignalDTO(signalDTO);

        // 验证车辆是否存在
        validateVehicleExists(signalDTO.getVid(), signalDTO.getChassisNumber());

        // 设置为未处理状态
        signalDTO.setIsProcessed(BatteryConstants.PROCESSED_NO);

        // 保存信号
        Signal signal = new Signal();
        BeanUtils.copyProperties(signalDTO, signal);
        parseSignalData(signal);
        signalMapper.insert(signal);

        // 更新最新信号缓存
        updateLatestSignalCache(signal);

        log.info("车辆信号上报成功，ID：{}，VID：{}", signal.getId(), signalDTO.getVid());
        return signal.getId();
    }

    @Override
    public SignalDTO getLatestSignalByVid(String vid) {
        log.info("获取车辆最新信号：{}", vid);

        ValidateUtils.requireNonBlank(vid, "VID不能为空");

        // 先从缓存查询
        String cacheKey = RedisConstants.CACHE_SIGNAL_LATEST + vid;
        SignalDTO cachedSignal = (SignalDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedSignal != null) {
            log.info("从缓存获取最新信号：{}", vid);
            return cachedSignal;
        }

        // 从数据库查询
        Signal signal = signalMapper.selectLatestByVid(vid);
        if (signal == null) {
            throw new BatteryException(ResponseCodeEnum.DATA_NOT_FOUND.getCode(), "未找到车辆信号：" + vid);
        }

        SignalDTO signalDTO = new SignalDTO();
        BeanUtils.copyProperties(signal, signalDTO);

        // 缓存最新信号
        redisTemplate.opsForValue().set(cacheKey, signalDTO, RedisConstants.CACHE_EXPIRE_SIGNAL, TimeUnit.SECONDS);

        return signalDTO;
    }

    @Override
    public List<SignalDTO> getUnprocessedSignals(Integer limit) {
        log.info("查询未处理的信号列表，限制数量：{}", limit);

        List<Signal> signalList = signalMapper.selectUnprocessedSignals(limit);
        return signalList.stream()
                .map(signal -> {
                    SignalDTO signalDTO = new SignalDTO();
                    BeanUtils.copyProperties(signal, signalDTO);
                    return signalDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessedStatus(List<Long> ids, Integer isProcessed) {
        log.info("批量更新信号处理状态，数量：{}，状态：{}", ids.size(), isProcessed);

        if (ids == null || ids.isEmpty()) {
            return;
        }

        signalMapper.updateProcessedStatus(ids, isProcessed);
        log.info("批量更新信号处理状态成功");
    }

    /**
     * 校验信号DTO
     */
    private void validateSignalDTO(SignalDTO signalDTO) {
        ValidateUtils.requireNonBlank(signalDTO.getVid(), "VID不能为空");
        ValidateUtils.requireNonBlank(signalDTO.getChassisNumber(), "车架编号不能为空");
        ValidateUtils.requireNonBlank(signalDTO.getSignalData(), "信号数据不能为空");

        if (!ValidateUtils.isValidVid(signalDTO.getVid())) {
            throw new BatteryException(ResponseCodeEnum.PARAM_ERROR.getCode(), "VID格式错误，必须是16位字母数字组合");
        }

        if (!JsonUtils.isValidJson(signalDTO.getSignalData())) {
            throw BatteryException.signalFormatError("信号数据格式错误，必须是有效的JSON格式");
        }
    }

    /**
     * 验证车辆是否存在
     */
    private void validateVehicleExists(String vid, String chassisNumber) {
        try {
            VehicleDTO vehicle = vehicleProvider.getVehicleByVid(vid);
            if (vehicle == null) {
                throw BatteryException.vehicleNotFound(vid);
            }

            // 验证车架编号是否匹配
            if (!vehicle.getChassisNumber().equals(chassisNumber)) {
                throw new BatteryException(ResponseCodeEnum.PARAM_ERROR.getCode(),
                        String.format("车架编号不匹配，VID：%s，期望：%s，实际：%s",
                                vid, vehicle.getChassisNumber(), chassisNumber));
            }
        } catch (Exception e) {
            log.error("验证车辆信息失败：{}，错误：{}", vid, e.getMessage());
            if (e instanceof BatteryException) {
                throw e;
            }
            throw new BatteryException(ResponseCodeEnum.SYSTEM_ERROR.getCode(), "验证车辆信息失败：" + e.getMessage());
        }
    }

    /**
     * 解析信号数据
     */
    private void parseSignalData(Signal signal) {
        try {
            Map<String, Object> signalMap = JsonUtils.parseMap(signal.getSignalData());
            if (signalMap != null) {
                // 提取电压数据
                Object mxObj = signalMap.get(BatteryConstants.SIGNAL_MX);
                Object miObj = signalMap.get(BatteryConstants.SIGNAL_MI);
                if (mxObj != null) {
                    signal.setMx(new BigDecimal(mxObj.toString()));
                }
                if (miObj != null) {
                    signal.setMi(new BigDecimal(miObj.toString()));
                }

                // 提取电流数据
                Object ixObj = signalMap.get(BatteryConstants.SIGNAL_IX);
                Object iiObj = signalMap.get(BatteryConstants.SIGNAL_II);
                if (ixObj != null) {
                    signal.setIx(new BigDecimal(ixObj.toString()));
                }
                if (iiObj != null) {
                    signal.setIi(new BigDecimal(iiObj.toString()));
                }
            }
        } catch (Exception e) {
            log.warn("解析信号数据失败：{}，错误：{}", signal.getSignalData(), e.getMessage());
        }
    }

    /**
     * 更新最新信号缓存
     */
    private void updateLatestSignalCache(Signal signal) {
        SignalDTO signalDTO = new SignalDTO();
        BeanUtils.copyProperties(signal, signalDTO);

        String cacheKey = RedisConstants.CACHE_SIGNAL_LATEST + signal.getVid();
        redisTemplate.opsForValue().set(cacheKey, signalDTO, RedisConstants.CACHE_EXPIRE_SIGNAL, TimeUnit.SECONDS);
    }
}
