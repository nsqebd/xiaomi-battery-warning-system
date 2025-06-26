package com.xiaomi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电池类型枚举
 */
@Getter
@AllArgsConstructor
public enum BatteryTypeEnum {

    TERNARY("三元电池", "ternary"),
    LFP("铁锂电池", "lfp");

    private final String typeName;
    private final String typeCode;

    public static BatteryTypeEnum getByTypeName(String typeName) {
        for (BatteryTypeEnum batteryType : values()) {
            if (batteryType.getTypeName().equals(typeName)) {
                return batteryType;
            }
        }
        return null;
    }

    public static boolean isValid(String typeName) {
        return getByTypeName(typeName) != null;
    }
}