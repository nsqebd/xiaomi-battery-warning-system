package com.xiaomi.common.exception;

import com.xiaomi.common.enums.ResponseCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电池系统业务异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BatteryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;

    public BatteryException(String message) {
        super(message);
        this.code = ResponseCodeEnum.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    public BatteryException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BatteryException(ResponseCodeEnum responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public BatteryException(ResponseCodeEnum responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    // 静态工厂方法
    public static BatteryException vehicleNotFound(String vid) {
        return new BatteryException(ResponseCodeEnum.VEHICLE_NOT_FOUND.getCode(), "车辆不存在：" + vid);
    }

    public static BatteryException ruleNotFound(String ruleCode) {
        return new BatteryException(ResponseCodeEnum.RULE_NOT_FOUND.getCode(), "规则不存在：" + ruleCode);
    }

    public static BatteryException signalFormatError(String detail) {
        return new BatteryException(ResponseCodeEnum.SIGNAL_FORMAT_ERROR.getCode(), detail);
    }
}
