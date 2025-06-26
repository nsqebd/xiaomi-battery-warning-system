package com.xiaomi.warning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomi.common.dto.WarningDTO;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.warning.entity.Warning;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 预警服务接口
 */
public interface WarningService extends IService<Warning> {

    /**
     * 新增预警
     */
    Long addWarning(WarningDTO warningDTO);

    /**
     * 更新预警
     */
    void updateWarning(Long id, WarningDTO warningDTO);

    /**
     * 删除预警
     */
    void deleteWarning(Long id);

    /**
     * 根据ID查询预警
     */
    WarningDTO getWarningById(Long id);

    /**
     * 分页查询预警列表
     */
    PageResult<WarningDTO> getWarningList(Long current, Long size, String vid,
                                          Integer warningLevel, Integer status,
                                          LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据VID查询预警列表
     */
    List<WarningDTO> getWarningsByVid(String vid);

    /**
     * 处理预警
     */
    void processWarning(Long id);

    /**
     * 获取预警统计信息
     */
    Map<String, Object> getWarningStats(LocalDateTime startTime, LocalDateTime endTime);
}
