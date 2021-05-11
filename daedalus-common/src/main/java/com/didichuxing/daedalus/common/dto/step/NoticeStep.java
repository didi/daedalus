package com.didichuxing.daedalus.common.dto.step;

import com.didichuxing.daedalus.common.enums.NoticeTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class NoticeStep extends BaseStep {

    @NotNull
    private NoticeTypeEnum noticeType;

    @NotNull
    @Size.List(@Size(min = 1))
    private List<String> receivers;

    @NotBlank
    private String noticeContent;


}
