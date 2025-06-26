package com.xiaomi.common.utils;

import com.xiaomi.common.constants.BatteryConstants;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * 数据校验工具类
 */
public class ValidateUtils {

    private static final Pattern VID_PATTERN = Pattern.compile("^[A-Z0-9]{16}$");

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotEmpty(str);
    }

    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    public static boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    public static boolean isValidVid(String vid) {
        return isNotBlank(vid) && VID_PATTERN.matcher(vid).matches();
    }

    public static boolean isValidBatteryType(String batteryType) {
        return BatteryConstants.BATTERY_TYPE_TERNARY.equals(batteryType) ||
                BatteryConstants.BATTERY_TYPE_LFP.equals(batteryType);
    }

    public static boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null) {
            return false;
        }
        boolean minValid = min == null || value.compareTo(min) >= 0;
        boolean maxValid = max == null || value.compareTo(max) <= 0;
        return minValid && maxValid;
    }

    public static boolean isValidBatteryHealth(BigDecimal batteryHealth) {
        return isInRange(batteryHealth, BigDecimal.ZERO, new BigDecimal("100"));
    }

    public static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNonBlank(String str, String message) {
        if (isBlank(str)) {
            throw new IllegalArgumentException(message);
        }
    }
}