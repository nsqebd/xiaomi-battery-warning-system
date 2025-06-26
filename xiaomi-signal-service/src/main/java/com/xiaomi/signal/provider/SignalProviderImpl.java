package com.xiaomi.signal.provider;

import com.xiaomi.common.dto.SignalDTO;
import com.xiaomi.signal.service.SignalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 信号服务Dubbo提供者
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class SignalProviderImpl implements SignalProvider {

    private final SignalService signalService;

    @Override
    public SignalDTO getLatestSignalByVid(String vid) {
        log.info("Dubbo调用：获取最新信号 - {}", vid);
        return signalService.getLatestSignalByVid(vid);
    }

    @Override
    public List<SignalDTO> getSignalsByVid(String vid) {
        log.info("Dubbo调用：根据VID查询信号列表 - {}", vid);
        return signalService.getSignalsByVid(vid);
    }

    @Override
    public SignalDTO getSignalById(Long id) {
        log.info("Dubbo调用：根据ID查询信号 - {}", id);
        return signalService.getSignalById(id);
    }
}