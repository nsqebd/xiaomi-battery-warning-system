package com.xiaomi.signal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomi.common.dto.SignalDTO;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.signal.entity.Signal;

import java.util.List;

/**
 * 信号服务接口
 */
public interface SignalService extends IService<Signal> {

    /**
     * 新增信号
     */
    Long addSignal(SignalDTO signalDTO);

    /**
     * 更新信号
     */
    void updateSignal(Long id, SignalDTO signalDTO);

    /**
     * 删除信号
     */
    void deleteSignal(Long id);

    /**
     * 根据ID查询信号
     */
    SignalDTO getSignalById(Long id);

    /**
     * 分页查询信号列表
     */
    PageResult<SignalDTO> getSignalList(Long current, Long size, String vid, Integer isProcessed);

    /**
     * 根据VID查询信号列表
     */
    List<SignalDTO> getSignalsByVid(String vid);

    /**
     * 车辆信号上报
     */
    Long reportSignal(SignalDTO signalDTO);

    /**
     * 获取车辆最新信号
     */
    SignalDTO getLatestSignalByVid(String vid);

    /**
     * 查询未处理的信号列表
     */
    List<SignalDTO> getUnprocessedSignals(Integer limit);

    /**
     * 批量更新信号处理状态
     */
    void updateProcessedStatus(List<Long> ids, Integer isProcessed);
}