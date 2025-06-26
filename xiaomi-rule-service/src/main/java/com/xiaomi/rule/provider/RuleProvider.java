package com.xiaomi.rule.provider;

import com.xiaomi.common.dto.RuleDTO;

import java.util.List;
import java.util.Map;

/**
 * 规则服务Dubbo接口
 */
public interface RuleProvider {

    /**
     * 根据电池类型查询启用的规则列表
     */
    List<RuleDTO> getEnabledRulesByBatteryType(String batteryType);

    /**
     * 根据规则编号和电池类型查询规则
     */
    RuleDTO getRuleByCodeAndBatteryType(String ruleCode, String batteryType);

    /**
     * 执行规则计算预警等级
     */
    Integer executeRule(String ruleCode, String batteryType, Map<String, Object> signalData);

    /**
     * 根据ID查询规则
     */
    RuleDTO getRuleById(Long id);
}