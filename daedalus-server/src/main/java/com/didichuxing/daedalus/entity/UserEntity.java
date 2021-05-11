package com.didichuxing.daedalus.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/25
 */
@Data
@Document("user")
public class UserEntity {

    @Id
    private String id;

    @Field
    @Indexed
    private String username;

    @Field
    private String usernameCN;

    /**
     * 收藏的流水线id
     */
    @Field
    private List<String> favorites;


}
