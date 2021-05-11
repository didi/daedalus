package com.didichuxing.daedalus.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/16
 */
@Data
public class PageResponse<T> extends Response<T> {

    private int currentPage;

    private int pageSize;

    private int totalPage;

    private long total;

    public static <T> PageResponse<T> of(int currentPage, int pageSize, int totalPage, long total, T data) {
        PageResponse<T> response = new PageResponse<>();
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setTotal(total);
        response.setTotalPage(totalPage);
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static <T> PageResponse<List<T>> of(Page<?> page, List<T> content) {
        PageResponse<List<T>> response = new PageResponse<>();
        response.setCurrentPage(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotal(page.getTotalElements());
        response.setTotalPage(page.getTotalPages());
        response.setData(content);
        response.setSuccess(true);
        return response;
    }
}
