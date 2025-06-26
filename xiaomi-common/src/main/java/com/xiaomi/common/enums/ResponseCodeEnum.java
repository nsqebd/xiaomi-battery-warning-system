package com.xiaomi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {

    SUCCESS(200, "ok", "操作成功"),
    SYSTEM_ERROR(500, "system_error", "系统内部错误"),
    PARAM_ERROR(400, "param_error", "参数错误"),
    DATA_NOT_FOUND(404, "data_not_found", "数据不存在"),
    DATA_EXIST(409, "data_exist", "数据已存在"),
    BUSINESS_ERROR(422, "business_error", "业务处理异常"),

    // 车辆相关
    VEHICLE_NOT_FOUND(1001, "vehicle_not_found", "车辆信息不存在"),
    VEHICLE_EXIST(1002, "vehicle_exist", "车辆信息已存在"),

    // 规则相关
    RULE_NOT_FOUND(2001, "rule_not_found", "规则信息不存在"),
    RULE_EXIST(2002, "rule_exist", "规则信息已存在"),
    RULE_PARSE_ERROR(2003, "rule_parse_error", "规则解析失败"),

    // 信号相关
    SIGNAL_FORMAT_ERROR(3001, "signal_format_error", "信号数据格式错误"),
    SIGNAL_DATA_MISSING(3002, "signal_data_missing", "信号数据缺失"),

    // 预警相关
    WARNING_GENERATE_ERROR(4001, "warning_generate_error", "预警生成失败"),
    WARNING_PROCESS_ERROR(4002, "warning_process_error", "预警处理失败");

    private final Integer code;
    private final String errorCode;
    private final String message;

    public static ResponseCodeEnum getByCode(Integer code) {
        for (ResponseCodeEnum responseCode : values()) {
            if (responseCode.getCode().equals(code)) {
                return responseCode;
            }
        }
        return null;
    }
}
