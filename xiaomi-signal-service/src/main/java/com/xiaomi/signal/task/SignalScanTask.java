package com.xiaomi.signal.task;

import com.xiaomi.common.constants.BatteryConstants;
import com.xiaomi.common.constants.WarningConstants;
import com.xiaomi.common.dto.SignalDTO;
import com.xiaomi.signal.mq.producer.SignalProducer;
import com.xiaomi.signal.service.SignalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 信号扫描定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SignalScanTask {

    private final SignalService signalService;
    private final SignalProducer signalProducer;

    @Value("${task.signal.scan.batch-size:100}")
    private Integer batchSize;

    /**
     * 扫描未处理的信号，发送到预警服务
     */
    @Scheduled(cron = "${task.signal.scan.cron:0 0 * * * ?}")
    public void scanUnprocessedSignals() {
        try {
            log.debug("开始扫描未处理的信号，批次大小：{}", batchSize);

            // 查询未处理的信号
            List<SignalDTO> unprocessedSignals = signalService.getUnprocessedSignals(batchSize);

            if (unprocessedSignals.isEmpty()) {
                log.debug("未找到需要处理的信号");
                return;
            }

            log.info("找到{}条未处理的信号", unprocessedSignals.size());

            // 发送信号到预警服务
            for (SignalDTO signal : unprocessedSignals) {
                try {
                    signalProducer.sendSignalForWarning(signal);
                    log.debug("信号发送成功：VID={}, ID={}", signal.getVid(), signal.getId());
                } catch (Exception e) {
                    log.error("信号发送失败：VID={}, ID={}, 错误：{}", signal.getVid(), signal.getId(), e.getMessage());
                }
            }

            // 批量更新信号处理状态
            List<Long> signalIds = unprocessedSignals.stream()
                    .map(SignalDTO::getId)
                    .collect(Collectors.toList());

            signalService.updateProcessedStatus(signalIds, BatteryConstants.PROCESSED_YES);

            log.info("信号扫描任务完成，处理了{}条信号", unprocessedSignals.size());

        } catch (Exception e) {
            log.error("信号扫描任务执行失败：{}", e.getMessage(), e);
        }
    }
}