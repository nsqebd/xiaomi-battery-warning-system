package com.xiaomi.rule.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 规则引擎服务接口
 */
public interface RuleEngineService {

    /**
     * 执行规则计算预警等级
     *
     * @param ruleCode    规则编号
     * @param batteryType 电池类型
     * @param signalData  信号数据
     * @return 预警等级，-1表示不报警
     */
    Integer executeRule(String ruleCode, String batteryType, Map<String, Object> signalData);

    /**
     * 计算电压差预警等级
     *
     * @param batteryType 电池类型
     * @param mx          最高电压
     * @param mi          最小电压
     * @return 预警等级
     */
    Integer calculateVoltageDiffLevel(String batteryType, BigDecimal mx, BigDecimal mi);

    /**
     * 计算电流差预警等级
     *
     * @param batteryType 电池类型
     * @param ix          最高电流
     * @param ii          最小电流
     * @return 预警等级
     */
    Integer calculateCurrentDiffLevel(String batteryType, BigDecimal ix, BigDecimal ii);
}