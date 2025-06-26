package com.xiaomi.rule.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 规则评估器
 */
@Slf4j
@Component
public class RuleEvaluator {

    /**
     * 评估条件是否满足
     *
     * @param condition  条件表达式
     * @param value      实际值
     * @return 是否满足条件
     */
    public boolean evaluate(String condition, BigDecimal value) {
        log.debug("评估条件：{}，值：{}", condition, value);

        if (value == null) {
            return false;
        }

        try {
            // 简单的条件评估逻辑
            if (condition.startsWith(">=")) {
                BigDecimal threshold = new BigDecimal(condition.substring(2));
                return value.compareTo(threshold) >= 0;
            } else if (condition.startsWith("<=")) {
                BigDecimal threshold = new BigDecimal(condition.substring(2));
                return value.compareTo(threshold) <= 0;
            } else if (condition.startsWith(">")) {
                BigDecimal threshold = new BigDecimal(condition.substring(1));
                return value.compareTo(threshold) > 0;
            } else if (condition.startsWith("<")) {
                BigDecimal threshold = new BigDecimal(condition.substring(1));
                return value.compareTo(threshold) < 0;
            } else if (condition.startsWith("=")) {
                BigDecimal threshold = new BigDecimal(condition.substring(1));
                return value.compareTo(threshold) == 0;
            }

            return false;
        } catch (Exception e) {
            log.error("条件评估失败：{}，错误：{}", condition, e.getMessage());
            return false;
        }
    }
}