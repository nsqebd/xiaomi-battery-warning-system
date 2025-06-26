package com.xiaomi.warning.service.impl;

import com.xiaomi.common.constants.BatteryConstants;
import com.xiaomi.common.constants.WarningConstants;
import com.xiaomi.common.dto.RuleDTO;
import com.xiaomi.common.dto.SignalDTO;
import com.xiaomi.common.dto.VehicleDTO;
import com.xiaomi.common.dto.WarningDTO;
import com.xiaomi.common.enums.WarningLevelEnum;
import com.xiaomi.common.utils.JsonUtils;
import com.xiaomi.rule.provider.RuleProvider;
import com.xiaomi.vehicle.provider.VehicleProvider;
import com.xiaomi.warning.mq.producer.WarningProducer;
import com.xiaomi.warning.service.WarningProcessService;
import com.xiaomi.warning.service.WarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 预警处理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarningProcessServiceImpl implements WarningProcessService {

    private final WarningService warningService;
    private final WarningProducer warningProducer;

    @DubboReference(version = "1.0.0", check = false)
    private VehicleProvider vehicleProvider;

    @DubboReference(version = "1.0.0", check = false)
    private RuleProvider ruleProvider;

    @Override
    public void processSignalForWarning(SignalDTO signalDTO) {
        log.info("处理信号数据生成预警：VID={}", signalDTO.getVid());

        try {
            // 获取车辆信息
            VehicleDTO vehicle = vehicleProvider.getVehicleByVid(signalDTO.getVid());
            if (vehicle == null) {
                log.warn("未找到车辆信息：{}", signalDTO.getVid());
                return;
            }

            // 获取适用的规则列表
            List<RuleDTO> rules = ruleProvider.getEnabledRulesByBatteryType(vehicle.getBatteryType());
            if (rules.isEmpty()) {
                log.warn("未找到适用的规则：电池类型={}", vehicle.getBatteryType());
                return;
            }

            // 解析信号数据
            Map<String, Object> signalMap = JsonUtils.parseMap(signalDTO.getSignalData());
            if (signalMap == null) {
                log.warn("信号数据解析失败：{}", signalDTO.getSignalData());
                return;
            }

            // 逐个执行规则
            for (RuleDTO rule : rules) {
                try {
                    Integer warningLevel = ruleProvider.executeRule(rule.getRuleCode(), vehicle.getBatteryType(), signalMap);

                    // 如果需要报警，生成预警记录
                    if (WarningLevelEnum.needWarning(warningLevel)) {
                        String warningMessage = generateWarningMessage(rule, signalMap, warningLevel);
                        WarningDTO warningDTO = generateWarning(signalDTO, rule.getRuleCode(), warningLevel, warningMessage);

                        // 发送预警通知
                        sendWarningNotification(warningDTO);

                        log.info("生成预警记录：VID={}, 规则={}, 等级={}",
                                signalDTO.getVid(), rule.getRuleName(), warningLevel);
                    } else {
                        log.debug("规则执行结果不需要报警：VID={}, 规则={}, 等级={}",
                                signalDTO.getVid(), rule.getRuleName(), warningLevel);
                    }

                } catch (Exception e) {
                    log.error("规则执行失败：VID={}, 规则={}, 错误：{}",
                            signalDTO.getVid(), rule.getRuleName(), e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("处理信号预警失败：VID={}, 错误：{}", signalDTO.getVid(), e.getMessage(), e);
        }
    }

    @Override
    public WarningDTO generateWarning(SignalDTO signalDTO, String ruleCode, Integer warningLevel, String warningMessage) {
        log.info("生成预警记录：VID={}, 规则={}, 等级={}", signalDTO.getVid(), ruleCode, warningLevel);

        try {
            // 获取车辆信息
            VehicleDTO vehicle = vehicleProvider.getVehicleByVid(signalDTO.getVid());

            // 获取规则信息
            RuleDTO rule = ruleProvider.getRuleByCodeAndBatteryType(ruleCode, vehicle.getBatteryType());

            // 构建预警DTO
            WarningDTO warningDTO = new WarningDTO();
            warningDTO.setVid(signalDTO.getVid());
            warningDTO.setChassisNumber(signalDTO.getChassisNumber());
            warningDTO.setBatteryType(vehicle.getBatteryType());
            warningDTO.setRuleCode(ruleCode);
            warningDTO.setRuleName(rule.getRuleName());
            warningDTO.setWarningLevel(warningLevel);
            warningDTO.setWarningLevelDesc(WarningLevelEnum.getLevelName(warningLevel));
            warningDTO.setSignalData(signalDTO.getSignalData());
            warningDTO.setWarningMessage(warningMessage);
            warningDTO.setWarningTime(LocalDateTime.now());
            warningDTO.setStatus(BatteryConstants.PROCESSED_NO);
            warningDTO.setStatusDesc("未处理");

            // 保存预警记录
            Long warningId = warningService.addWarning(warningDTO);
            warningDTO.setId(warningId);

            log.info("预警记录生成成功：ID={}", warningId);
            return warningDTO;

        } catch (Exception e) {
            log.error("生成预警记录失败：VID={}, 错误：{}", signalDTO.getVid(), e.getMessage(), e);
            throw new RuntimeException("生成预警记录失败：" + e.getMessage(), e);
        }
    }

    @Override
    public void sendWarningNotification(WarningDTO warningDTO) {
        log.info("发送预警通知：ID={}, VID={}, 等级={}",
                warningDTO.getId(), warningDTO.getVid(), warningDTO.getWarningLevel());

        try {
            // 发送预警结果消息
            warningProducer.sendWarningResult(warningDTO);

            // 根据预警等级发送不同类型的通知
            if (warningDTO.getWarningLevel() <= WarningConstants.WARNING_LEVEL_HIGH) {
                // 高危预警，发送紧急通知
                warningProducer.sendUrgentNotification(warningDTO);
            }

            log.info("预警通知发送成功：ID={}", warningDTO.getId());

        } catch (Exception e) {
            log.error("发送预警通知失败：ID={}, 错误：{}", warningDTO.getId(), e.getMessage(), e);
        }
    }

    /**
     * 生成预警消息
     */
    private String generateWarningMessage(RuleDTO rule, Map<String, Object> signalMap, Integer warningLevel) {
        StringBuilder message = new StringBuilder();
        message.append(rule.getRuleName()).append("：");

        // 根据规则类型生成具体消息
        if (BatteryConstants.RULE_CODE_VOLTAGE.equals(rule.getRuleCode())) {
            Object mx = signalMap.get(BatteryConstants.SIGNAL_MX);
            Object mi = signalMap.get(BatteryConstants.SIGNAL_MI);
            if (mx != null && mi != null) {
                double diff = Double.parseDouble(mx.toString()) - Double.parseDouble(mi.toString());
                message.append(String.format("电压差%.3fV", diff));
            }
        } else if (BatteryConstants.RULE_CODE_CURRENT.equals(rule.getRuleCode())) {
            Object ix = signalMap.get(BatteryConstants.SIGNAL_IX);
            Object ii = signalMap.get(BatteryConstants.SIGNAL_II);
            if (ix != null && ii != null) {
                double diff = Double.parseDouble(ix.toString()) - Double.parseDouble(ii.toString());
                message.append(String.format("电流差%.3fA", diff));
            }
        }

        message.append("，预警等级：").append(warningLevel);
        message.append("（").append(WarningLevelEnum.getLevelName(warningLevel)).append("）");

        return message.toString();
    }
}
