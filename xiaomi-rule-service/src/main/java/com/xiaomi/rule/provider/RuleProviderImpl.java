package com.xiaomi.rule.provider;

import com.xiaomi.common.dto.RuleDTO;
import com.xiaomi.rule.service.RuleEngineService;
import com.xiaomi.rule.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.Map;

/**
 * 规则服务Dubbo提供者
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class RuleProviderImpl implements RuleProvider {

    private final RuleService ruleService;
    private final RuleEngineService ruleEngineService;

    @Override
    public List<RuleDTO> getEnabledRulesByBatteryType(String batteryType) {
        log.info("Dubbo调用：根据电池类型查询启用规则 - {}", batteryType);
        return ruleService.getEnabledRulesByBatteryType(batteryType);
    }

    @Override
    public RuleDTO getRuleByCodeAndBatteryType(String ruleCode, String batteryType) {
        log.info("Dubbo调用：根据规则编号和电池类型查询规则 - {}-{}", ruleCode, batteryType);
        return ruleService.getRuleByCodeAndBatteryType(ruleCode, batteryType);
    }

    @Override
    public Integer executeRule(String ruleCode, String batteryType, Map<String, Object> signalData) {
        log.info("Dubbo调用：执行规则 - {}-{}，信号数据：{}", ruleCode, batteryType, signalData);
        return ruleEngineService.executeRule(ruleCode, batteryType, signalData);
    }

    @Override
    public RuleDTO getRuleById(Long id) {
        log.info("Dubbo调用：根据ID查询规则 - {}", id);
        return ruleService.getRuleById(id);
    }
}