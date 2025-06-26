package com.xiaomi.warning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预警实体类
 */
@Data
@TableName("warning")
public class Warning implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车辆识别码
     */
    private String vid;

    /**
     * 车架编号
     */
    private String chassisNumber;

    /**
     * 电池类型
     */
    private String batteryType;

    /**
     * 规则编号
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 预警等级：0-最高响应，1-高风险，2-中风险，3-低风险，4-很低风险
     */
    private Integer warningLevel;

    /**
     * 触发预警的信号数据
     */
    private String signalData;

    /**
     * 预警信息描述
     */
    private String warningMessage;

    /**
     * 预警时间
     */
    private LocalDateTime warningTime;

    /**
     * 处理状态：0-未处理，1-已处理
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}