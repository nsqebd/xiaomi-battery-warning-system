package com.xiaomi.signal.mq.consumer;

import com.xiaomi.common.config.RocketMQConfig;
import com.xiaomi.common.dto.WarningDTO;
import com.xiaomi.common.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 预警结果消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = RocketMQConfig.Topics.WARNING_RESULT_TOPIC,
        consumerGroup = "warning-result-consumer-group"
)
public class WarningConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        try {
            log.info("接收到预警结果消息：{}", message);

            WarningDTO warningDTO = JsonUtils.parseObject(message, WarningDTO.class);
            if (warningDTO == null) {
                log.error("预警结果消息解析失败：{}", message);
                return;
            }

            // 处理预警结果
            processWarningResult(warningDTO);

        } catch (Exception e) {
            log.error("处理预警结果消息失败：{}，错误：{}", message, e.getMessage(), e);
            throw new RuntimeException("处理预警结果消息失败", e);
        }
    }

    /**
     * 处理预警结果
     */
    private void processWarningResult(WarningDTO warningDTO) {
        log.info("处理预警结果：VID={}，等级={}，消息={}",
                warningDTO.getVid(), warningDTO.getWarningLevel(), warningDTO.getWarningMessage());

        // 这里可以做一些预警结果的后续处理
        // 例如：更新信号处理状态、发送通知等

        log.info("预警结果处理完成：{}", warningDTO.getVid());
    }
}