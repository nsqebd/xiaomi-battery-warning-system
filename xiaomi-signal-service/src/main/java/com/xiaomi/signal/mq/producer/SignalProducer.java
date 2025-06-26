package com.xiaomi.signal.mq.producer;

import com.xiaomi.common.config.RocketMQConfig;
import com.xiaomi.common.dto.SignalDTO;
import com.xiaomi.common.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

/**
 * 信号消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SignalProducer {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 发送信号到预警服务
     */
    public void sendSignalForWarning(SignalDTO signalDTO) {
        try {
            String topic = RocketMQConfig.Topics.SIGNAL_WARNING_TOPIC;
            String tag = RocketMQConfig.Tags.SIGNAL_REPORT;
            String destination = topic + ":" + tag;

            String message = JsonUtils.toJsonString(signalDTO);
            log.info("发送信号到预警服务，VID：{}，目标：{}", signalDTO.getVid(), destination);

            rocketMQTemplate.convertAndSend(destination, message);
            log.info("信号消息发送成功：{}", signalDTO.getVid());

        } catch (Exception e) {
            log.error("发送信号消息失败：{}，错误：{}", signalDTO.getVid(), e.getMessage(), e);
            throw new RuntimeException("发送信号消息失败：" + e.getMessage(), e);
        }
    }
}