package com.xiaomi.warning.provider;

import com.xiaomi.common.dto.WarningDTO;
import com.xiaomi.warning.service.WarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 预警服务Dubbo提供者
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class WarningProviderImpl implements WarningProvider {

    private final WarningService warningService;

    @Override
    public List<WarningDTO> getWarningsByVid(String vid) {
        log.info("Dubbo调用：根据VID查询预警列表 - {}", vid);
        return warningService.getWarningsByVid(vid);
    }

    @Override
    public WarningDTO getWarningById(Long id) {
        log.info("Dubbo调用：根据ID查询预警 - {}", id);
        return warningService.getWarningById(id);
    }

    @Override
    public void processWarning(Long id) {
        log.info("Dubbo调用：处理预警 - {}", id);
        warningService.processWarning(id);
    }
}