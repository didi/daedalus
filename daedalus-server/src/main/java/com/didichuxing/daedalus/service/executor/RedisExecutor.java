package com.didichuxing.daedalus.service.executor;

import com.alibaba.fastjson.JSON;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.common.enums.redis.RedisOperationType;
import com.didichuxing.daedalus.entity.step.RedisStepEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.service.InstanceCenter;
import com.didichuxing.daedalus.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Component
@Slf4j
public class RedisExecutor extends Executor<RedisStepEntity> {
    @Autowired
    private InstanceCenter instanceCenter;

    @Override
    protected void exec(PipelineContext pipelineContext, RedisStepEntity step) {

        String instanceId = step.getInstanceId();
        String command = step.getCommand();
        String[] multiCommands = command.split("\n");
        String result = null;
        for (String singleCommand : multiCommands) {
            singleCommand = singleCommand.trim().replaceAll("\\s+", " ");
            result = executeRedis(pipelineContext, step, instanceId, singleCommand);
        }
        createResponse(pipelineContext, step, result);


    }

    private String executeRedis(PipelineContext pipelineContext, RedisStepEntity step, String instanceId, String command) {
        try (Jedis jedis = instanceCenter.getRedis(instanceId)) {
            String redisCommand = RedisUtil.getCommand(command);
            Protocol.Command protocolCommand = RedisOperationType.getCommand(redisCommand);
            if (protocolCommand == null) {
                throw new ExecuteException("不支持命令:" + redisCommand);
            }
            Object result = jedis.sendCommand(protocolCommand, RedisUtil.getArgs(command));
            String processResult = JSON.toJSONString(RedisOperationType.getBuilder(protocolCommand).build(result));

            appendLog(pipelineContext, step, "执行Redis命令" + command + ";结果:" + processResult);
            log.info("redis command:{} 执行结果:{}", command, processResult);
            return processResult;

        } catch (IllegalArgumentException e) {
            log.error("redis 执行出错！", e);
            throw new ExecuteException(e.getMessage());
        } catch (Exception e) {
            log.error("redis 执行出错！", e);
            if (e.getMessage().startsWith("ERR wrong number of arguments")) {
                appendLog(pipelineContext, step, "Redis命令参数个数错误！");
            }
            throw new ExecuteException("Redis执行失败！" + e.getMessage());
        }
    }


    @Override
    public StepTypeEnum getStepType() {
        return StepTypeEnum.REDIS;
    }


}
