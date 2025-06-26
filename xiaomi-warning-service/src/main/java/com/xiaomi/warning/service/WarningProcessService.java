package com.xiaomi.warning.service;

import com.xiaomi.common.dto.SignalDTO;
import com.xiaomi.common.dto.WarningDTO;

/**
 * 预警处理服务接口
 */
public interface WarningProcessService {

    /**
     * 处理信号数据，生成预警
     */
    void processSignalForWarning(SignalDTO signalDTO);

    /**
     * 生成预警记录
     */
    WarningDTO generateWarning(SignalDTO signalDTO, String ruleCode, Integer warningLevel, String warningMessage);

    /**
     * 发送预警通知
     */
    void sendWarningNotification(WarningDTO warningDTO);
}