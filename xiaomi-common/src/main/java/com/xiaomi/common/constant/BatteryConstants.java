package com.xiaomi.common.constants;

/**
 * 电池相关常量
 */
public class BatteryConstants {

    /**
     * 电池类型
     */
    public static final String BATTERY_TYPE_TERNARY = "三元电池";
    public static final String BATTERY_TYPE_LFP = "铁锂电池";

    /**
     * 信号类型
     */
    public static final String SIGNAL_MX = "Mx";  // 最高电压
    public static final String SIGNAL_MI = "Mi";  // 最小电压
    public static final String SIGNAL_IX = "Ix";  // 最高电流
    public static final String SIGNAL_II = "Ii";  // 最小电流

    /**
     * 规则编号
     */
    public static final String RULE_CODE_VOLTAGE = "1";  // 电压差报警
    public static final String RULE_CODE_CURRENT = "2";  // 电流差报警

    /**
     * 状态常量
     */
    public static final int STATUS_DISABLED = 0;  // 禁用
    public static final int STATUS_ENABLED = 1;   // 启用
    public static final int DELETE_NO = 0;        // 未删除
    public static final int DELETE_YES = 1;       // 已删除
    public static final int PROCESSED_NO = 0;     // 未处理
    public static final int PROCESSED_YES = 1;    // 已处理
}