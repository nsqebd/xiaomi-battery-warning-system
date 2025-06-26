package com.xiaomi.common.constants;

/**
 * 预警相关常量
 */
public class WarningConstants {

    /**
     * 预警等级
     */
    public static final int WARNING_LEVEL_CRITICAL = 0;  // 严重
    public static final int WARNING_LEVEL_HIGH = 1;      // 高
    public static final int WARNING_LEVEL_MEDIUM = 2;    // 中
    public static final int WARNING_LEVEL_LOW = 3;       // 低
    public static final int WARNING_LEVEL_INFO = 4;      // 信息
    public static final int WARNING_LEVEL_NO_WARNING = -1; // 不报警

    /**
     * 预警等级描述
     */
    public static final String WARNING_LEVEL_CRITICAL_DESC = "严重";
    public static final String WARNING_LEVEL_HIGH_DESC = "高";
    public static final String WARNING_LEVEL_MEDIUM_DESC = "中";
    public static final String WARNING_LEVEL_LOW_DESC = "低";
    public static final String WARNING_LEVEL_INFO_DESC = "信息";

    /**
     * 定时任务相关
     */
    public static final String TASK_SIGNAL_SCAN_CRON = "0/30 * * * * ?";  // 每30秒扫描一次
    public static final int TASK_SIGNAL_SCAN_BATCH_SIZE = 100;            // 批处理大小
}