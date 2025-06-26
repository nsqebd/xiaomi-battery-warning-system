package com.xiaomi.warning.mq.producer;

import com.xiaomi.common.config.RocketMQConfig;
import com.xiaomi.common.dto.WarningDTO;
import com.xiaomi.common.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

/**
 * 预警消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WarningProducer {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 发送预警结果消息
     */
    public void sendWarningResult(WarningDTO warningDTO) {
        try {
            String topic = RocketMQConfig.Topics.WARNING_RESULT_TOPIC;
            String tag = RocketMQConfig.Tags.WARNING_GENERATE;
            String destination = topic + ":" + tag;

            String message = JsonUtils.toJsonString(warningDTO);
            log.info("发送预警结果消息，VID：{}，等级：{}，目标：{}",
                    warningDTO.getVid(), warningDTO.getWarningLevel(), destination);

            rocketMQTemplate.convertAndSend(destination, message);
            log.info("预警结果消息发送成功：ID={}", warningDTO.getId());

        } catch (Exception e) {
            log.error("发送预警结果消息失败：ID={}, 错误：{}", warningDTO.getId(), e.getMessage(), e);
            throw new RuntimeException("发送预警结果消息失败：" + e.getMessage(), e);
        }
    }

    /**
     * 发送紧急预警通知
     */
    public void sendUrgentNotification(WarningDTO warningDTO) {
        try {
            String topic = "urgent_warning_topic";
            String tag = "urgent_notification";
            String destination = topic + ":" + tag;

            String message = JsonUtils.toJsonString(warningDTO);
            log.info("发送紧急预警通知，VID：{}，等级：{}，目标：{}",
                    warningDTO.getVid(), warningDTO.getWarningLevel(), destination);

            rocketMQTemplate.convertAndSend(destination, message);
            log.info("紧急预警通知发送成功：ID={}", warningDTO.getId());

        } catch (Exception e) {
            log.error("发送紧急预警通知失败：ID={}, 错误：{}", warningDTO.getId(), e.getMessage(), e);
        }
    }
}