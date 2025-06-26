package com.xiaomi.signal.provider;

import com.xiaomi.common.dto.SignalDTO;

import java.util.List;

/**
 * 信号服务Dubbo接口
 */
public interface SignalProvider {

    /**
     * 获取车辆最新信号
     */
    SignalDTO getLatestSignalByVid(String vid);

    /**
     * 根据VID查询信号列表
     */
    List<SignalDTO> getSignalsByVid(String vid);

    /**
     * 根据ID查询信号
     */
    SignalDTO getSignalById(Long id);
}