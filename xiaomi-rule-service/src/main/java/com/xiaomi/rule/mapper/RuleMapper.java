package com.xiaomi.rule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomi.rule.entity.Rule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规则Mapper接口
 */
@Mapper
public interface RuleMapper extends BaseMapper<Rule> {

    /**
     * 根据规则编号和电池类型查询规则
     */
    Rule selectByRuleCodeAndBatteryType(@Param("ruleCode") String ruleCode, @Param("batteryType") String batteryType);

    /**
     * 根据电池类型查询启用的规则列表
     */
    List<Rule> selectEnabledRulesByBatteryType(@Param("batteryType") String batteryType);

    /**
     * 分页查询规则列表
     */
    IPage<Rule> selectRulePage(Page<Rule> page, @Param("batteryType") String batteryType, @Param("status") Integer status);

    /**
     * 根据序号查询规则
     */
    Rule selectBySequenceNo(@Param("sequenceNo") Integer sequenceNo);
}
