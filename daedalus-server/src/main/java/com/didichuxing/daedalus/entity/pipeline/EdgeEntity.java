package com.didichuxing.daedalus.entity.pipeline;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 连线
 *
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Data
@NoArgsConstructor
public class EdgeEntity {

    @Field
    private String source;

    @Field
    private String target;

    /**
     * 连线文字
     */
    @Field
    private String label;

    public EdgeEntity(String source, String target) {
        this.source = source;
        this.target = target;
    }
}
