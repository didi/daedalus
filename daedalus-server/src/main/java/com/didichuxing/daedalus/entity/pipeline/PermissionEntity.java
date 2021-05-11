package com.didichuxing.daedalus.entity.pipeline;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author jiangxinyu
 */
@Data
public class PermissionEntity {

    /**
     * 0 全部  1 创建人 2 列表的人
     */
    @Field
    public Integer visible;

    @Field
    private List<String> runners;


    /**
     * 0 全部  1 创建人 2 列表的人
     */
    @Field
    public Integer editable;

    @Field
    private List<String> editors;


}