package com.didichuxing.daedalus.entity.directory;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/11/24
 */
@Data
@Document("directory")
public class DirectoryEntity {


    @Id
    private String id;

    @Field
    @Indexed(unique = true)
    private String username;

    @Field
    private String usernameCN;


    @Field
    private List<SubNodeEntity> directories;

    @Field
    private List<String> unfoldNodes;

}
