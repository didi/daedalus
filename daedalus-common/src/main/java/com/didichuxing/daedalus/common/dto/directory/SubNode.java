package com.didichuxing.daedalus.common.dto.directory;

import lombok.Data;

import java.util.List;

/**
 * 目录子节点 ，可能是目录 或者流水线
 *
 * @author : jiangxinyu
 * @date : 2020/11/25
 */
@Data
public class SubNode {

    private String id;

    private String parentId;

    private String name;

    private String pipelineId;

    private List<SubNode> children;

    /**
     * pipeline 或directory
     */
    private String type;

}
