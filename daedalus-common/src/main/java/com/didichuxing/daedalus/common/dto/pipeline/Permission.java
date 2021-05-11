package com.didichuxing.daedalus.common.dto.pipeline;

import lombok.Data;

import java.util.List;

/**
 * @author jiangxinyu
 */
@Data
public class Permission {

    /**
     * 0 全部  1 创建人 2 列表的人
     */
    private Integer visible;

    private List<String> runners;

    /**
     * 0 全部  1 创建人 2 列表的人
     */
    private Integer editable;

    private List<String> editors;


}