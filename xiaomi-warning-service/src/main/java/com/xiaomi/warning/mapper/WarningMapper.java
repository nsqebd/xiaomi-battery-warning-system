package com.xiaomi.warning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomi.warning.entity.Warning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 预警Mapper接口
 */
@Mapper
public interface WarningMapper extends BaseMapper<Warning> {

    /**
     * 根据VID查询预警列表
     */
    List<Warning> selectByVid(@Param("vid") String vid);

    /**
     * 分页查询预警列表
     */
    IPage<Warning> selectWarningPage(Page<Warning> page,
                                     @Param("vid") String vid,
                                     @Param("warningLevel") Integer warningLevel,
                                     @Param("status") Integer status,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 查询预警统计信息
     */
    List<Map<String, Object>> selectWarningStats(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 根据预警等级统计数量
     */
    List<Map<String, Object>> selectWarningCountByLevel(@Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 根据车辆统计预警数量
     */
    List<Map<String, Object>> selectWarningCountByVehicle(@Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);
}