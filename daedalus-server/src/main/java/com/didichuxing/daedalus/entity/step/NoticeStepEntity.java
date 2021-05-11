package com.didichuxing.daedalus.entity.step;

import com.didichuxing.daedalus.common.enums.NoticeTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class NoticeStepEntity extends BaseStepEntity {


    @Field
    private NoticeTypeEnum noticeType;
    @Field
    private List<String> receivers;
    @Field
    private String noticeContent;


}
