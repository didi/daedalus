package com.didichuxing.daedalus.entity.directory;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2021/1/4
 */
@Document("directoryShare")
@Data
public class DirectoryShareEntity {

    @Id
    private String id;

    @Field
    @Indexed
    private String linkId;

    @Field
    private String sharer;

    @Field
    private List<SubNodeEntity> shareNodes;

    @Field
    @CreatedDate
    private Date createTime;

}
