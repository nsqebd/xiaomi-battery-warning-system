package com.xiaomi.warning.search;

import com.xiaomi.common.dto.WarningDTO;
import com.xiaomi.common.result.PageResult;

import java.util.Map;

/**
 * 预警搜索服务接口
 */
public interface WarningSearchService {

    /**
     * 索引预警数据
     */
    void indexWarning(WarningDTO warningDTO);

    /**
     * 删除预警索引
     */
    void deleteWarningIndex(Long warningId);

    /**
     * 搜索预警信息
     */
    PageResult<WarningDTO> searchWarnings(String keyword, Integer warningLevel,
                                          Long current, Long size);

    /**
     * 高级搜索
     */
    PageResult<WarningDTO> advancedSearch(Map<String, Object> searchParams);
}