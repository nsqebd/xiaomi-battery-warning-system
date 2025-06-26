package com.xiaomi.warning.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomi.common.constants.BatteryConstants;
import com.xiaomi.common.constants.RedisConstants;
import com.xiaomi.common.dto.WarningDTO;
import com.xiaomi.common.enums.ResponseCodeEnum;
import com.xiaomi.common.enums.WarningLevelEnum;
import com.xiaomi.common.exception.BatteryException;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.common.utils.ValidateUtils;
import com.xiaomi.warning.entity.Warning;
import com.xiaomi.warning.mapper.WarningMapper;
import com.xiaomi.warning.service.WarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 预警服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarningServiceImpl extends ServiceImpl<WarningMapper, Warning> implements WarningService {

    private final WarningMapper warningMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addWarning(WarningDTO warningDTO) {
        log.info("新增预警：{}-{}", warningDTO.getVid(), warningDTO.getRuleName());

        // 参数校验
        validateWarningDTO(warningDTO);

        // 保存预警
        Warning warning = new Warning();
        BeanUtils.copyProperties(warningDTO, warning);
        warningMapper.insert(warning);

        // 更新缓存
        updateWarningCache(warning);

        log.info("新增预警成功，ID：{}", warning.getId());
        return warning.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarning(Long id, WarningDTO warningDTO) {
        log.info("更新预警：{}", id);

        // 检查预警是否存在
        Warning existWarning = warningMapper.selectById(id);
        if (existWarning == null) {
            throw new BatteryException(ResponseCodeEnum.DATA_NOT_FOUND.getCode(), "预警不存在：" + id);
        }

        // 参数校验
        validateWarningDTO(warningDTO);

        // 更新预警
        Warning warning = new Warning();
        BeanUtils.copyProperties(warningDTO, warning);
        warning.setId(id);
        warningMapper.updateById(warning);

        // 删除缓存
        clearWarningCache(existWarning);

        // 更新缓存
        Warning updatedWarning = warningMapper.selectById(id);
        updateWarningCache(updatedWarning);

        log.info("更新预警成功：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWarning(Long id) {
        log.info("删除预警：{}", id);

        // 检查预警是否存在
        Warning existWarning = warningMapper.selectById(id);
        if (existWarning == null) {
            throw new BatteryException(ResponseCodeEnum.DATA_NOT_FOUND.getCode(), "预警不存在：" + id);
        }

        // 物理删除
        warningMapper.deleteById(id);

        // 删除缓存
        clearWarningCache(existWarning);

        log.info("删除预警成功：{}", id);
    }

    @Override
    public WarningDTO getWarningById(Long id) {
        log.info("根据ID查询预警：{}", id);

        // 先从缓存查询
        String cacheKey = RedisConstants.CACHE_WARNING_INFO + id;
        WarningDTO cachedWarning = (WarningDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedWarning != null) {
            log.info("从缓存获取预警信息：{}", id);
            return cachedWarning;
        }

        // 从数据库查询
        Warning warning = warningMapper.selectById(id);
        if (warning == null) {
            throw new BatteryException(ResponseCodeEnum.DATA_NOT_FOUND.getCode(), "预警不存在：" + id);
        }

        WarningDTO warningDTO = convertToDTO(warning);

        // 缓存预警信息
        redisTemplate.opsForValue().set(cacheKey, warningDTO, RedisConstants.CACHE_EXPIRE_WARNING, TimeUnit.SECONDS);

        return warningDTO;
    }

    @Override
    public PageResult<WarningDTO> getWarningList(Long current, Long size, String vid,
                                                 Integer warningLevel, Integer status,
                                                 LocalDateTime startTime, LocalDateTime endTime) {
        log.info("分页查询预警列表，页码：{}，大小：{}，VID：{}，等级：{}，状态：{}",
                current, size, vid, warningLevel, status);

        Page<Warning> page = new Page<>(current, size);
        IPage<Warning> warningPage = warningMapper.selectWarningPage(page, vid, warningLevel, status, startTime, endTime);

        List<WarningDTO> warningDTOList = warningPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(warningDTOList, warningPage.getTotal(), current, size);
    }

    @Override
    public List<WarningDTO> getWarningsByVid(String vid) {
        log.info("根据VID查询预警列表：{}", vid);

        ValidateUtils.requireNonBlank(vid, "VID不能为空");

        List<Warning> warningList = warningMapper.selectByVid(vid);
        return warningList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processWarning(Long id) {
        log.info("处理预警：{}", id);

        // 检查预警是否存在
        Warning existWarning = warningMapper.selectById(id);
        if (existWarning == null) {
            throw new BatteryException(ResponseCodeEnum.DATA_NOT_FOUND.getCode(), "预警不存在：" + id);
        }

        // 检查预警状态
        if (BatteryConstants.PROCESSED_YES == existWarning.getStatus()) {
            throw new BatteryException(ResponseCodeEnum.BUSINESS_ERROR.getCode(), "预警已处理：" + id);
        }

        // 更新处理状态
        Warning warning = new Warning();
        warning.setId(id);
        warning.setStatus(BatteryConstants.PROCESSED_YES);
        warningMapper.updateById(warning);

        // 删除缓存
        clearWarningCache(existWarning);

        log.info("处理预警成功：{}", id);
    }

    @Override
    public Map<String, Object> getWarningStats(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取预警统计信息：{} - {}", startTime, endTime);

        Map<String, Object> stats = new HashMap<>();

        // 基础统计
        List<Map<String, Object>> basicStats = warningMapper.selectWarningStats(startTime, endTime);
        if (!basicStats.isEmpty()) {
            Map<String, Object> basicStat = basicStats.get(0);
            stats.put("totalCount", basicStat.get("total_count"));
            stats.put("processedCount", basicStat.get("processed_count"));
            stats.put("unprocessedCount", basicStat.get("unprocessed_count"));
        } else {
            stats.put("totalCount", 0);
            stats.put("processedCount", 0);
            stats.put("unprocessedCount", 0);
        }

        // 按等级统计
        List<Map<String, Object>> levelStats = warningMapper.selectWarningCountByLevel(startTime, endTime);
        stats.put("levelStats", levelStats);

        // 按车辆统计
        List<Map<String, Object>> vehicleStats = warningMapper.selectWarningCountByVehicle(startTime, endTime);
        stats.put("vehicleStats", vehicleStats);

        return stats;
    }

    /**
     * 校验预警DTO
     */
    private void validateWarningDTO(WarningDTO warningDTO) {
        ValidateUtils.requireNonBlank(warningDTO.getVid(), "VID不能为空");
        ValidateUtils.requireNonBlank(warningDTO.getChassisNumber(), "车架编号不能为空");
        ValidateUtils.requireNonBlank(warningDTO.getBatteryType(), "电池类型不能为空");
        ValidateUtils.requireNonBlank(warningDTO.getRuleCode(), "规则编号不能为空");
        ValidateUtils.requireNonBlank(warningDTO.getRuleName(), "规则名称不能为空");
        ValidateUtils.requireNonNull(warningDTO.getWarningLevel(), "预警等级不能为空");

        if (!ValidateUtils.isValidVid(warningDTO.getVid())) {
            throw new BatteryException(ResponseCodeEnum.PARAM_ERROR.getCode(), "VID格式错误，必须是16位字母数字组合");
        }

        if (!ValidateUtils.isValidBatteryType(warningDTO.getBatteryType())) {
            throw new BatteryException(ResponseCodeEnum.PARAM_ERROR.getCode(), "电池类型无效，只能是：三元电池、铁锂电池");
        }

        if (!WarningLevelEnum.needWarning(warningDTO.getWarningLevel())) {
            throw new BatteryException(ResponseCodeEnum.PARAM_ERROR.getCode(), "预警等级无效：" + warningDTO.getWarningLevel());
        }
    }

    /**
     * 转换为DTO
     */
    private WarningDTO convertToDTO(Warning warning) {
        WarningDTO warningDTO = new WarningDTO();
        BeanUtils.copyProperties(warning, warningDTO);

        // 设置预警等级描述
        warningDTO.setWarningLevelDesc(WarningLevelEnum.getLevelName(warning.getWarningLevel()));

        // 设置状态描述
        if (BatteryConstants.PROCESSED_YES == warning.getStatus()) {
            warningDTO.setStatusDesc("已处理");
        } else {
            warningDTO.setStatusDesc("未处理");
        }

        return warningDTO;
    }

    /**
     * 更新预警缓存
     */
    private void updateWarningCache(Warning warning) {
        WarningDTO warningDTO = convertToDTO(warning);

        String cacheKey = RedisConstants.CACHE_WARNING_INFO + warning.getId();
        redisTemplate.opsForValue().set(cacheKey, warningDTO, RedisConstants.CACHE_EXPIRE_WARNING, TimeUnit.SECONDS);
    }

    /**
     * 清除预警缓存
     */
    private void clearWarningCache(Warning warning) {
        redisTemplate.delete(RedisConstants.CACHE_WARNING_INFO + warning.getId());
    }
}