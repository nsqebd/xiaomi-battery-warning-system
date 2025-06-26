package com.xiaomi.vehicle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆实体类
 */
@Data
@TableName("vehicle")
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车辆识别码，16位随机字符串
     */
    private String vid;

    /**
     * 车架编号
     */
    private String chassisNumber;

    /**
     * 电池类型：三元电池、铁锂电池
     */
    private String batteryType;

    /**
     * 总里程(km)
     */
    private BigDecimal totalMileage;

    /**
     * 电池健康状态(%)
     */
    private BigDecimal batteryHealth;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDeleted;
}