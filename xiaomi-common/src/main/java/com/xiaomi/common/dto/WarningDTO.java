package com.xiaomi.common.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预警DTO
 */
@Data
@Schema(description = "预警DTO")
public class WarningDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "车辆识别码不能为空")
    @Schema(description = "车辆识别码", example = "XM001A2B3C4D5E6F")
    private String vid;

    @NotBlank(message = "车架编号不能为空")
    @Schema(description = "车架编号", example = "1")
    private String chassisNumber;

    @NotBlank(message = "电池类型不能为空")
    @Schema(description = "电池类型", example = "三元电池")
    private String batteryType;

    @NotBlank(message = "规则编号不能为空")
    @Schema(description = "规则编号", example = "1")
    private String ruleCode;

    @NotBlank(message = "规则名称不能为空")
    @Schema(description = "规则名称", example = "电压差报警")
    private String ruleName;

    @NotNull(message = "预警等级不能为空")
    @Schema(description = "预警等级：0-最高响应", example = "0")
    private Integer warningLevel;

    @Schema(description = "预警等级描述", example = "严重")
    private String warningLevelDesc;

    @Schema(description = "触发预警的信号数据JSON字符串")
    private String signalData;

    @Schema(description = "预警信息描述")
    private String warningMessage;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "预警时间")
    private LocalDateTime warningTime;

    @Schema(description = "处理状态：0-未处理，1-已处理", example = "0")
    private Integer status;

    @Schema(description = "处理状态描述", example = "未处理")
    private String statusDesc;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
