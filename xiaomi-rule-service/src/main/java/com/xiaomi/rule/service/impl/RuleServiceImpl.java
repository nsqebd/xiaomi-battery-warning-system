package com.xiaomi.rule.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomi.common.constants.RedisConstants;
import com.xiaomi.common.dto.RuleDTO;
import com.xiaomi.common.enums.ResponseCodeEnum;
import com.xiaomi.common.exception.BatteryException;
import com.xiaomi.common.result.PageResult;
import com.xiaomi.common.utils.ValidateUtils;
import com.xiaomi.rule.entity.Rule;
import com.xiaomi.rule.mapper.RuleMapper;
import com.xiaomi.rule.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 规则服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleServiceImpl extends ServiceImpl<RuleMapper, Rule> implements RuleService {

    private final RuleMapper ruleMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addRule(RuleDTO ruleDTO) {
        log.info("新增规则：{}-{}", ruleDTO.getRuleCode(), ruleDTO.getBatteryType());

        // 参数校验
        validateRuleDTO(ruleDTO);

        // 检查序号是否已存在
        Rule existRule = ruleMapper.selectBySequenceNo(ruleDTO.getSequenceNo());
        if (existRule != null) {
            throw new BatteryException(ResponseCodeEnum.RULE_EXIST.getCode(), "序号已存在：" + ruleDTO.getSequenceNo());
        }

        // 检查规则编号和电池类型组合是否已存在
        existRule = ruleMapper.selectByRuleCodeAndBatteryType(ruleDTO.getRuleCode(), ruleDTO.getBatteryType());
        if (existRule != null) {
            throw new BatteryException(ResponseCodeEnum.RULE_EXIST.getCode(),
                    String.format("规则已存在：%s-%s", ruleDTO.getRuleCode(), ruleDTO.getBatteryType()));
        }

        // 保存规则
        Rule rule = new Rule();
        BeanUtils.copyProperties(ruleDTO, rule);
        ruleMapper.insert(rule);

        // 更新缓存
        updateRuleCache(rule);

        log.info("新增规则成功，ID：{}", rule.getId());
        return rule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRule(Long id, RuleDTO ruleDTO) {
        log.info("更新规则：{}", id);

        // 检查规则是否存在
        Rule existRule = ruleMapper.selectById(id);
        if (existRule == null) {
            throw new BatteryException(ResponseCodeEnum.RULE_NOT_FOUND.getCode(), "规则不存在：" + id);
        }

        // 参数校验
        validateRuleDTO(ruleDTO);

        // 如果序号发生变化，检查新序号是否已存在
        if (!existRule.getSequenceNo().equals(ruleDTO.getSequenceNo())) {
            Rule rule = ruleMapper.selectBySequenceNo(ruleDTO.getSequenceNo());
            if (rule != null && !rule.getId().equals(id)) {
                throw new BatteryException(ResponseCodeEnum.RULE_EXIST.getCode(), "序号已存在：" + ruleDTO.getSequenceNo());
            }
        }

        // 如果规则编号或电池类型发生变化，检查新组合是否已存在
        if (!existRule.getRuleCode().equals(ruleDTO.getRuleCode()) ||
                !existRule.getBatteryType().equals(ruleDTO.getBatteryType())) {
            Rule rule = ruleMapper.selectByRuleCodeAndBatteryType(ruleDTO.getRuleCode(), ruleDTO.getBatteryType());
            if (rule != null && !rule.getId().equals(id)) {
                throw new BatteryException(ResponseCodeEnum.RULE_EXIST.getCode(),
                        String.format("规则已存在：%s-%s", ruleDTO.getRuleCode(), ruleDTO.getBatteryType()));
            }
        }

        // 更新规则
        Rule rule = new Rule();
        BeanUtils.copyProperties(ruleDTO, rule);
        rule.setId(id);
        ruleMapper.updateById(rule);

        // 删除旧缓存
        clearRuleCache(existRule);

        // 更新新缓存
        Rule updatedRule = ruleMapper.selectById(id);
        updateRuleCache(updatedRule);

        log.info("更新规则成功：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRule(Long id) {
        log.info("删除规则：{}", id);

        // 检查规则是否存在
        Rule existRule = ruleMapper.selectById(id);
        if (existRule == null) {
            throw new BatteryException(ResponseCodeEnum.RULE_NOT_FOUND.getCode(), "规则不存在：" + id);
        }

        // 逻辑删除
        ruleMapper.deleteById(id);

        // 删除缓存
        clearRuleCache(existRule);

        log.info("删除规则成功：{}", id);
    }

    @Override
    public RuleDTO getRuleById(Long id) {
        log.info("根据ID查询规则：{}", id);

        // 先从缓存查询
        String cacheKey = RedisConstants.CACHE_RULE_INFO + id;
        RuleDTO cachedRule = (RuleDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedRule != null) {
            log.info("从缓存获取规则信息：{}", id);
            return cachedRule;
        }

        // 从数据库查询
        Rule rule = ruleMapper.selectById(id);
        if (rule == null) {
            throw new BatteryException(ResponseCodeEnum.RULE_NOT_FOUND.getCode(), "规则不存在：" + id);
        }

        RuleDTO ruleDTO = new RuleDTO();
        BeanUtils.copyProperties(rule, ruleDTO);

        // 缓存规则信息
        redisTemplate.opsForValue().set(cacheKey, ruleDTO, RedisConstants.CACHE_EXPIRE_RULE, TimeUnit.SECONDS);

        return ruleDTO;
    }

    @Override
    public PageResult<RuleDTO> getRuleList(Long current, Long size, String batteryType, Integer status) {
        log.info("分页查询规则列表，页码：{}，大小：{}，电池类型：{}，状态：{}", current, size, batteryType, status);

        Page<Rule> page = new Page<>(current, size);
        IPage<Rule> rulePage = ruleMapper.selectRulePage(page, batteryType, status);

        List<RuleDTO> ruleDTOList = rulePage.getRecords().stream()
                .map(rule -> {
                    RuleDTO ruleDTO = new RuleDTO();
                    BeanUtils.copyProperties(rule, ruleDTO);
                    return ruleDTO;
                })
                .collect(Collectors.toList());

        return PageResult.of(ruleDTOList, rulePage.getTotal(), current, size);
    }

    @Override
    public List<RuleDTO> getEnabledRulesByBatteryType(String batteryType) {
        log.info("根据电池类型查询启用的规则：{}", batteryType);

        ValidateUtils.requireNonBlank(batteryType, "电池类型不能为空");

        // 先从缓存查询
        String cacheKey = RedisConstants.CACHE_RULE_BATTERY_TYPE + batteryType;
        @SuppressWarnings("unchecked")
        List<RuleDTO> cachedRules = (List<RuleDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedRules != null) {
            log.info("从缓存获取规则列表：{}", batteryType);
            return cachedRules;
        }

        // 从数据库查询
        List<Rule> ruleList = ruleMapper.selectEnabledRulesByBatteryType(batteryType);
        List<RuleDTO> ruleDTOList = ruleList.stream()
                .map(rule -> {
                    RuleDTO ruleDTO = new RuleDTO();
                    BeanUtils.copyProperties(rule, ruleDTO);
                    return ruleDTO;
                })
                .collect(Collectors.toList());

        // 缓存规则列表
        redisTemplate.opsForValue().set(cacheKey, ruleDTOList, RedisConstants.CACHE_EXPIRE_RULE, TimeUnit.SECONDS);

        return ruleDTOList;
    }

    @Override
    public RuleDTO getRuleByCodeAndBatteryType(String ruleCode, String batteryType) {
        log.info("根据规则编号和电池类型查询规则：{}-{}", ruleCode, batteryType);

        ValidateUtils.requireNonBlank(ruleCode, "规则编号不能为空");
        ValidateUtils.requireNonBlank(batteryType, "电池类型不能为空");

        Rule rule = ruleMapper.selectByRuleCodeAndBatteryType(ruleCode, batteryType);
        if (rule == null) {
            throw new BatteryException(ResponseCodeEnum.RULE_NOT_FOUND.getCode(),
                    String.format("规则不存在：%s-%s", ruleCode, batteryType));
        }

        RuleDTO ruleDTO = new RuleDTO();
        BeanUtils.copyProperties(rule, ruleDTO);

        return ruleDTO;
    }

    /**
     * 校验规则DTO
     */
    private void validateRuleDTO(RuleDTO ruleDTO) {
        ValidateUtils.requireNonNull(ruleDTO.getSequenceNo(), "序号不能为空");
        ValidateUtils.requireNonBlank(ruleDTO.getRuleCode(), "规则编号不能为空");
        ValidateUtils.requireNonBlank(ruleDTO.getRuleName(), "规则名称不能为空");
        ValidateUtils.requireNonBlank(ruleDTO.getBatteryType(), "电池类型不能为空");
        ValidateUtils.requireNonBlank(ruleDTO.getWarningRule(), "预警规则不能为空");

        if (!ValidateUtils.isValidBatteryType(ruleDTO.getBatteryType())) {
            throw new BatteryException(ResponseCodeEnum.PARAM_ERROR.getCode(), "电池类型无效，只能是：三元电池、铁锂电池");
        }
    }

    /**
     * 更新规则缓存
     */
    private void updateRuleCache(Rule rule) {
        RuleDTO ruleDTO = new RuleDTO();
        BeanUtils.copyProperties(rule, ruleDTO);

        // 缓存规则信息
        redisTemplate.opsForValue().set(RedisConstants.CACHE_RULE_INFO + rule.getId(),
                ruleDTO, RedisConstants.CACHE_EXPIRE_RULE, TimeUnit.SECONDS);

        // 清除电池类型相关的缓存列表
        redisTemplate.delete(RedisConstants.CACHE_RULE_BATTERY_TYPE + rule.getBatteryType());
    }

    /**
     * 清除规则缓存
     */
    private void clearRuleCache(Rule rule) {
        redisTemplate.delete(RedisConstants.CACHE_RULE_INFO + rule.getId());
        redisTemplate.delete(RedisConstants.CACHE_RULE_BATTERY_TYPE + rule.getBatteryType());
    }
}
