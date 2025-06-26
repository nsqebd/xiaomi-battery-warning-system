package com.xiaomi.warning.mq.consumer;

import com.xiaomi.common.config.RocketMQConfig;
import com.xiaomi.common.dto.SignalDTO;
import com.xiaomi.common.utils.JsonUtils;
import com.xiaomi.warning.service.WarningProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 信号消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = RocketMQConfig.Topics.SIGNAL_WARNING_TOPIC,
        consumerGroup = "signal-warning-consumer-group"
)
public class SignalConsumer implements RocketMQListener<String> {

    private final WarningProcessService warningProcessService;

    @Override
    public void onMessage(String message) {
        try {
            log.info("接收到信号消息：{}", message);

            SignalDTO signalDTO = JsonUtils.parseObject(message, SignalDTO.class);
            if (signalDTO == null) {
                log.error("信号消息解析失败：{}", message);
                return;
            }

            // 处理信号数据，生成预警
            warningProcessService.processSignalForWarning(signalDTO);

            log.info("信号消息处理完成：VID={}", signalDTO.getVid());

        } catch (Exception e) {
            log.error("处理信号消息失败：{}，错误：{}", message, e.getMessage(), e);
            throw new RuntimeException("处理信号消息失败", e);
        }
    }
}