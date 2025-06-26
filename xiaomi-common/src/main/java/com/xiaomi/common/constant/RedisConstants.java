package com.xiaomi.common.constants;

/**
 * Redis相关常量
 */
public class RedisConstants {

    /**
     * 缓存键前缀
     */
    public static final String CACHE_PREFIX = "xiaomi:battery:";

    /**
     * 车辆信息缓存
     */
    public static final String CACHE_VEHICLE_INFO = CACHE_PREFIX + "vehicle:info:";
    public static final String CACHE_VEHICLE_VID = CACHE_PREFIX + "vehicle:vid:";
    public static final String CACHE_VEHICLE_CHASSIS = CACHE_PREFIX + "vehicle:chassis:";

    /**
     * 规则缓存
     */
    public static final String CACHE_RULE_INFO = CACHE_PREFIX + "rule:info:";
    public static final String CACHE_RULE_BATTERY_TYPE = CACHE_PREFIX + "rule:type:";

    /**
     * 信号缓存
     */
    public static final String CACHE_SIGNAL_LATEST = CACHE_PREFIX + "signal:latest:";

    /**
     * 预警缓存
     */
    public static final String CACHE_WARNING_INFO = CACHE_PREFIX + "warning:info:";

    /**
     * 分布式锁
     */
    public static final String LOCK_SIGNAL_PROCESS = CACHE_PREFIX + "lock:signal:process:";
    public static final String LOCK_WARNING_GENERATE = CACHE_PREFIX + "lock:warning:generate:";

    /**
     * 缓存过期时间（秒）
     */
    public static final long CACHE_EXPIRE_VEHICLE = 3600;     // 车辆信息 1小时
    public static final long CACHE_EXPIRE_RULE = 7200;       // 规则信息 2小时
    public static final long CACHE_EXPIRE_SIGNAL = 1800;     // 信号信息 30分钟
    public static final long CACHE_EXPIRE_WARNING = 3600;    // 预警信息 1小时
}