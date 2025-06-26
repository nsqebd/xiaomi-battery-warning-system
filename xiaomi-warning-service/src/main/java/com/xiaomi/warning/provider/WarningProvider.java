package com.xiaomi.warning.provider;

import com.xiaomi.common.dto.WarningDTO;

import java.util.List;

/**
 * 预警服务Dubbo接口
 */
public interface WarningProvider {

    /**
     * 根据VID查询预警列表
     */
    List<WarningDTO> getWarningsByVid(String vid);

    /**
     * 根据ID查询预警
     */
    WarningDTO getWarningById(Long id);

    /**
     * 处理预警
     */
    void processWarning(Long id);
}