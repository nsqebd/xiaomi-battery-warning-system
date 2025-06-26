package com.xiaomi.common.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 规则DTO
 */
@Data
@Schema(description = "规则DTO")
public class RuleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotNull(message = "序号不能为空")
    @Schema(description = "序号", example = "1")
    private Integer sequenceNo;

    @NotBlank(message = "规则编号不能为空")
    @Schema(description = "规则编号", example = "1")
    private String ruleCode;

    @NotBlank(message = "规则名称不能为空")
    @Schema(description = "规则名称", example = "电压差报警")
    private String ruleName;

    @NotBlank(message = "电池类型不能为空")
    @Pattern(regexp = "三元电池|铁锂电池", message = "电池类型只能是：三元电池、铁锂电池")
    @Schema(description = "电池类型", example = "三元电池")
    private String batteryType;

    @NotBlank(message = "预警规则不能为空")
    @Schema(description = "预警规则描述")
    private String warningRule;

    @Schema(description = "规则表达式JSON字符串")
    private String ruleExpression;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否删除", example = "0")
    private Integer isDeleted;
}