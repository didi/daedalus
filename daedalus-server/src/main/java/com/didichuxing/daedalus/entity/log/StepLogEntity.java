package com.didichuxing.daedalus.entity.log;

import com.didichuxing.daedalus.common.enums.StepStatusEnum;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/28
 */
@Setter
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class StepLogEntity {

    @Field
    @NonNull
    private String stepId;

    @Field
    @NonNull
    private String stepName;

    @Field
    @NonNull
    private StepStatusEnum stepStatus = StepStatusEnum.NOT_RUN;


    @Field
    private StepResponseEntity stepResponse;

    @Field
    private List<String> logs;
}
