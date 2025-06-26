package com.xiaomi.signal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 信号实体类
 */
@Data
@TableName("signal")
public class Signal implements Serializable {

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
     * 信号数据(JSON格式)
     */
    private String signalData;

    /**
     * 最高电压
     */
    private BigDecimal mx;

    /**
     * 最小电压
     */
    private BigDecimal mi;

    /**
     * 最高电流
     */
    private BigDecimal ix;

    /**
     * 最小电流
     */
    private BigDecimal ii;

    /**
     * 上报时间
     */
    private LocalDateTime reportTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否已处理：0-未处理，1-已处理
     */
    private Integer isProcessed;
}