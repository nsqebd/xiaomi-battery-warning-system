package com.xiaomi.rule.service.impl;

import com.xiaomi.common.constants.BatteryConstants;
import com.xiaomi.common.constants.WarningConstants;
import com.xiaomi.common.dto.RuleDTO;
import com.xiaomi.common.exception.BatteryException;
import com.xiaomi.common.utils.JsonUtils;
import com.xiaomi.rule.service.RuleEngineService;
import com.xiaomi.rule.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 规则引擎服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEngineServiceImpl implements RuleEngineService {

    private final RuleService ruleService;

    @Override
    public Integer executeRule(String ruleCode, String batteryType, Map<String, Object> signalData) {
        log.info("执行规则：{}-{}，信号数据：{}", ruleCode, batteryType, signalData);

        try {
            // 获取规则
            RuleDTO rule = ruleService.getRuleByCodeAndBatteryType(ruleCode, batteryType);

            // 根据规则编号执行相应的计算逻辑
            if (BatteryConstants.RULE_CODE_VOLTAGE.equals(ruleCode)) {
                return executeVoltageDiffRule(rule, signalData);
            } else if (BatteryConstants.RULE_CODE_CURRENT.equals(ruleCode)) {
                return executeCurrentDiffRule(rule, signalData);
            } else {
                throw new BatteryException("不支持的规则类型：" + ruleCode);
            }

        } catch (Exception e) {
            log.error("规则执行失败：{}-{}，错误：{}", ruleCode, batteryType, e.getMessage(), e);
            throw new BatteryException("规则执行失败：" + e.getMessage());
        }
    }

    @Override
    public Integer calculateVoltageDiffLevel(String batteryType, BigDecimal mx, BigDecimal mi) {
        log.info("计算电压差预警等级：{}，Mx={}，Mi={}", batteryType, mx, mi);

        if (mx == null || mi == null) {
            log.warn("电压数据不完整，无法计算预警等级");
            return WarningConstants.WARNING_LEVEL_NO_WARNING;
        }

        BigDecimal diff = mx.subtract(mi);
        log.info("电压差：{}", diff);

        // 根据电池类型和电压差计算预警等级
        if (BatteryConstants.BATTERY_TYPE_TERNARY.equals(batteryType)) {
            // 三元电池电压差阈值
            if (diff.compareTo(new BigDecimal("5")) >= 0) {
                return WarningConstants.WARNING_LEVEL_CRITICAL;
            } else if (diff.compareTo(new BigDecimal("3")) >= 0) {
                return WarningConstants.WARNING_LEVEL_HIGH;
            } else if (diff.compareTo(new BigDecimal("1")) >= 0) {
                return WarningConstants.WARNING_LEVEL_MEDIUM;
            } else if (diff.compareTo(new BigDecimal("0.6")) >= 0) {
                return WarningConstants.WARNING_LEVEL_LOW;
            } else if (diff.compareTo(new BigDecimal("0.2")) >= 0) {
                return WarningConstants.WARNING_LEVEL_INFO;
            } else {
                return WarningConstants.WARNING_LEVEL_NO_WARNING;
            }
        } else if (BatteryConstants.BATTERY_TYPE_LFP.equals(batteryType)) {
            // 铁锂电池电压差阈值
            if (diff.compareTo(new BigDecimal("2")) >= 0) {
                return WarningConstants.WARNING_LEVEL_CRITICAL;
            } else if (diff.compareTo(new BigDecimal("1")) >= 0) {
                return WarningConstants.WARNING_LEVEL_HIGH;
            } else if (diff.compareTo(new BigDecimal("0.7")) >= 0) {
                return WarningConstants.WARNING_LEVEL_MEDIUM;
            } else if (diff.compareTo(new BigDecimal("0.4")) >= 0) {
                return WarningConstants.WARNING_LEVEL_LOW;
            } else if (diff.compareTo(new BigDecimal("0.2")) >= 0) {
                return WarningConstants.WARNING_LEVEL_INFO;
            } else {
                return WarningConstants.WARNING_LEVEL_NO_WARNING;
            }
        } else {
            throw new BatteryException("不支持的电池类型：" + batteryType);
        }
    }

    @Override
    public Integer calculateCurrentDiffLevel(String batteryType, BigDecimal ix, BigDecimal ii) {
        log.info("计算电流差预警等级：{}，Ix={}，Ii={}", batteryType, ix, ii);

        if (ix == null || ii == null) {
            log.warn("电流数据不完整，无法计算预警等级");
            return WarningConstants.WARNING_LEVEL_NO_WARNING;
        }

        BigDecimal diff = ix.subtract(ii);
        log.info("电流差：{}", diff);

        // 根据电池类型和电流差计算预警等级
        if (BatteryConstants.BATTERY_TYPE_TERNARY.equals(batteryType)) {
            // 三元电池电流差阈值
            if (diff.compareTo(new BigDecimal("3")) >= 0) {
                return WarningConstants.WARNING_LEVEL_CRITICAL;
            } else if (diff.compareTo(new BigDecimal("1")) >= 0) {
                return WarningConstants.WARNING_LEVEL_HIGH;
            } else if (diff.compareTo(new BigDecimal("0.2")) >= 0) {
                return WarningConstants.WARNING_LEVEL_MEDIUM;
            } else {
                return WarningConstants.WARNING_LEVEL_NO_WARNING;
            }
        } else if (BatteryConstants.BATTERY_TYPE_LFP.equals(batteryType)) {
            // 铁锂电池电流差阈值
            if (diff.compareTo(new BigDecimal("1")) >= 0) {
                return WarningConstants.WARNING_LEVEL_CRITICAL;
            } else if (diff.compareTo(new BigDecimal("0.5")) >= 0) {
                return WarningConstants.WARNING_LEVEL_HIGH;
            } else if (diff.compareTo(new BigDecimal("0.2")) >= 0) {
                return WarningConstants.WARNING_LEVEL_MEDIUM;
            } else {
                return WarningConstants.WARNING_LEVEL_NO_WARNING;
            }
        } else {
            throw new BatteryException("不支持的电池类型：" + batteryType);
        }
    }

    /**
     * 执行电压差规则
     */
    private Integer executeVoltageDiffRule(RuleDTO rule, Map<String, Object> signalData) {
        log.info("执行电压差规则：{}", rule.getRuleName());

        // 从信号数据中提取电压值
        Object mxObj = signalData.get(BatteryConstants.SIGNAL_MX);
        Object miObj = signalData.get(BatteryConstants.SIGNAL_MI);

        if (mxObj == null || miObj == null) {
            log.warn("信号数据中缺少电压信息：Mx={}, Mi={}", mxObj, miObj);
            return WarningConstants.WARNING_LEVEL_NO_WARNING;
        }

        BigDecimal mx = new BigDecimal(mxObj.toString());
        BigDecimal mi = new BigDecimal(miObj.toString());

        return calculateVoltageDiffLevel(rule.getBatteryType(), mx, mi);
    }

    /**
     * 执行电流差规则
     */
    private Integer executeCurrentDiffRule(RuleDTO rule, Map<String, Object> signalData) {
        log.info("执行电流差规则：{}", rule.getRuleName());

        // 从信号数据中提取电流值
        Object ixObj = signalData.get(BatteryConstants.SIGNAL_IX);
        Object iiObj = signalData.get(BatteryConstants.SIGNAL_II);

        if (ixObj == null || iiObj == null) {
            log.warn("信号数据中缺少电流信息：Ix={}, Ii={}", ixObj, iiObj);
            return WarningConstants.WARNING_LEVEL_NO_WARNING;
        }

        BigDecimal ix = new BigDecimal(ixObj.toString());
        BigDecimal ii = new BigDecimal(iiObj.toString());

        return calculateCurrentDiffLevel(rule.getBatteryType(), ix, ii);
    }
}
