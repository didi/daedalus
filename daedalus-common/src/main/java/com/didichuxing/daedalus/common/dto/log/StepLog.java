package com.didichuxing.daedalus.common.dto.log;

import com.didichuxing.daedalus.common.enums.StepStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/28
 */
@Setter
@Getter
public class StepLog {

    private String stepId;

    private String stepName;

    //todo 放这里？还是放在step里？
    private StepStatusEnum stepStatus = StepStatusEnum.NOT_RUN;

    private StepResponse stepResponse;

    private List<String> logs;
}
