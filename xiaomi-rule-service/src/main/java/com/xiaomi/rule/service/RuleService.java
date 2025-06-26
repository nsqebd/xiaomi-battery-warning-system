package com.xiaomi.rule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomi.common.dto.RuleDTO;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.rule.entity.Rule;

import java.util.List;

/**
 * 规则服务接口
 */
public interface RuleService extends IService<Rule> {

    /**
     * 新增规则
     */
    Long addRule(RuleDTO ruleDTO);

    /**
     * 更新规则
     */
    void updateRule(Long id, RuleDTO ruleDTO);

    /**
     * 删除规则
     */
    void deleteRule(Long id);

    /**
     * 根据ID查询规则
     */
    RuleDTO getRuleById(Long id);

    /**
     * 分页查询规则列表
     */
    PageResult<RuleDTO> getRuleList(Long current, Long size, String batteryType, Integer status);

    /**
     * 根据电池类型查询启用的规则列表
     */
    List<RuleDTO> getEnabledRulesByBatteryType(String batteryType);

    /**
     * 根据规则编号和电池类型查询规则
     */
    RuleDTO getRuleByCodeAndBatteryType(String ruleCode, String batteryType);
}
