package com.xiaomi.signal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomi.signal.entity.Signal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 信号Mapper接口
 */
@Mapper
public interface SignalMapper extends BaseMapper<Signal> {

    /**
     * 根据VID查询信号列表
     */
    List<Signal> selectByVid(@Param("vid") String vid);

    /**
     * 获取VID的最新信号
     */
    Signal selectLatestByVid(@Param("vid") String vid);

    /**
     * 分页查询信号列表
     */
    IPage<Signal> selectSignalPage(Page<Signal> page, @Param("vid") String vid, @Param("isProcessed") Integer isProcessed);

    /**
     * 查询未处理的信号列表（用于定时任务）
     */
    List<Signal> selectUnprocessedSignals(@Param("limit") Integer limit);

    /**
     * 批量更新信号处理状态
     */
    int updateProcessedStatus(@Param("ids") List<Long> ids, @Param("isProcessed") Integer isProcessed);
}
