package com.xiaomi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 预警等级枚举
 */
@Getter
@AllArgsConstructor
public enum WarningLevelEnum {

    CRITICAL(0, "严重", "#FF0000"),
    HIGH(1, "高", "#FF6600"),
    MEDIUM(2, "中", "#FFCC00"),
    LOW(3, "低", "#00CC00"),
    INFO(4, "信息", "#0066CC"),
    NO_WARNING(-1, "不报警", "#CCCCCC");

    private final Integer level;
    private final String levelName;
    private final String color;

    public static WarningLevelEnum getByLevel(Integer level) {
        for (WarningLevelEnum warningLevel : values()) {
            if (warningLevel.getLevel().equals(level)) {
                return warningLevel;
            }
        }
        return null;
    }

    public static String getLevelName(Integer level) {
        WarningLevelEnum warningLevel = getByLevel(level);
        return warningLevel != null ? warningLevel.getLevelName() : "未知";
    }

    public static boolean needWarning(Integer level) {
        return level != null && level >= 0;
    }
}