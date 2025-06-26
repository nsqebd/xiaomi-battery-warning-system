package com.xiaomi.common.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 信号DTO
 */
@Data
@Schema(description = "信号DTO")
public class SignalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "车辆识别码不能为空")
    @Schema(description = "车辆识别码", example = "XM001A2B3C4D5E6F")
    private String vid;

    @NotBlank(message = "车架编号不能为空")
    @Schema(description = "车架编号", example = "1")
    private String chassisNumber;

    @NotBlank(message = "信号数据不能为空")
    @Schema(description = "信号数据JSON字符串")
    private String signalData;

    @Schema(description = "最高电压", example = "12.0")
    private BigDecimal mx;

    @Schema(description = "最小电压", example = "0.6")
    private BigDecimal mi;

    @Schema(description = "最高电流", example = "12.0")
    private BigDecimal ix;

    @Schema(description = "最小电流", example = "11.7")
    private BigDecimal ii;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "上报时间")
    private LocalDateTime reportTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "是否已处理", example = "0")
    private Integer isProcessed;
}