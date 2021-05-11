package com.didichuxing.daedalus.handler;

import com.didichuxing.daedalus.common.dto.log.Log;
import com.didichuxing.daedalus.common.dto.log.StepLog;
import com.didichuxing.daedalus.common.dto.log.StepResponse;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.entity.log.StepLogEntity;
import com.didichuxing.daedalus.entity.log.StepResponseEntity;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/4/28
 */
@UtilityClass
public class LogConverter {

    public static Log entityToDto(LogEntity logEntity) {
        if (logEntity == null) {
            return null;
        }
        Log log = SimpleConverter.convert(logEntity, Log.class);

        List<StepLogEntity> stepLogs = logEntity.getStepLogs();
        if (stepLogs != null) {
            List<StepLog> stepLogsDto = stepLogs.stream()
                    .map(stepLogEntity -> {
                        StepLog stepLog = SimpleConverter.convert(stepLogEntity, StepLog.class);
                        stepLog.setStepResponse(SimpleConverter.convert(stepLogEntity.getStepResponse(), StepResponse.class));
                        return stepLog;
                    })
                    .collect(Collectors.toList());
            log.setStepLogs(stepLogsDto);
        }
        return log;
    }

    public static LogEntity dtoToEntity(Log log) {
        if (log == null) {
            return null;
        }
        LogEntity logEntity = SimpleConverter.convert(log, LogEntity.class);
        List<StepLog> stepLogs = log.getStepLogs();
        if (stepLogs != null) {
            List<StepLogEntity> stepLogEntities = stepLogs.stream()
                    .map(stepLogg -> {
                        StepLogEntity stepLog = SimpleConverter.convert(stepLogg, StepLogEntity.class);
                        stepLog.setStepResponse(SimpleConverter.convert(stepLogg.getStepResponse(), StepResponseEntity.class));
                        return stepLog;
                    })
                    .collect(Collectors.toList());
            logEntity.setStepLogs(stepLogEntities);
        }
        return logEntity;
    }
}
