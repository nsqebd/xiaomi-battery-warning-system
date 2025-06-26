package com.xiaomi.common.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆信息DTO
 */
@Data
@Schema(description = "车辆信息DTO")
public class VehicleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "车辆识别码不能为空")
    @Size(min = 16, max = 16, message = "车辆识别码必须是16位")
    @Schema(description = "车辆识别码", example = "XM001A2B3C4D5E6F")
    private String vid;

    @NotBlank(message = "车架编号不能为空")
    @Schema(description = "车架编号", example = "1")
    private String chassisNumber;

    @NotBlank(message = "电池类型不能为空")
    @Pattern(regexp = "三元电池|铁锂电池", message = "电池类型只能是：三元电池、铁锂电池")
    @Schema(description = "电池类型", example = "三元电池")
    private String batteryType;

    @NotNull(message = "总里程不能为空")
    @DecimalMin(value = "0.0", message = "总里程不能小于0")
    @Schema(description = "总里程(km)", example = "100.00")
    private BigDecimal totalMileage;

    @NotNull(message = "电池健康状态不能为空")
    @DecimalMin(value = "0.0", message = "电池健康状态不能小于0")
    @DecimalMax(value = "100.0", message = "电池健康状态不能超过100")
    @Schema(description = "电池健康状态(%)", example = "100.00")
    private BigDecimal batteryHealth;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否删除", example = "0")
    private Integer isDeleted;
}
