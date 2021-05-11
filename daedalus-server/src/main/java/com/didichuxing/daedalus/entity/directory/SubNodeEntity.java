package com.didichuxing.daedalus.entity.directory;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 目录子节点 ，可能是目录 或者流水线
 *
 * @author : jiangxinyu
 * @date : 2020/11/25
 */
@Data
@NoArgsConstructor
public class SubNodeEntity {

    @Field
    private String id;

    @Field
    private String parentId;

    @Field
    private String name;

    @Field
    private String pipelineId;

    @Field
    private List<SubNodeEntity> children;

    /**
     * pipeline 或directory
     */
    @Field
    private String type;

}
