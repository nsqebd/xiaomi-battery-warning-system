package com.xiaomi.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 */
@Data
@Schema(description = "分页响应结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "数据列表")
    private List<T> records;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页码")
    private Long current;

    @Schema(description = "每页大小")
    private Long size;

    @Schema(description = "总页数")
    private Long pages;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = calculatePages(total, size);
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        return new PageResult<>(records, total, current, size);
    }

    public static <T> PageResult<T> empty(Long current, Long size) {
        return new PageResult<>(List.of(), 0L, current, size);
    }

    private Long calculatePages(Long total, Long size) {
        if (total == null || total <= 0 || size == null || size <= 0) {
            return 0L;
        }
        return (total + size - 1) / size;
    }
}