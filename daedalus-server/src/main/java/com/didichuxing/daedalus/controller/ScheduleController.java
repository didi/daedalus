package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.PageResponse;
import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.schedule.Schedule;
import com.didichuxing.daedalus.common.enums.ScheduleStatusEnum;
import com.didichuxing.daedalus.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@RestController
@RequestMapping("schedule")
@Api(tags = "定时任务")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;


    @PostMapping("save")
    public Response<String> save(@Valid @RequestBody Schedule schedule) {
        String scheduleId = scheduleService.save(schedule);
        return Response.sucResp("保存成功！", scheduleId);
    }


    @PostMapping("switch")
    public Response<String> switchSchedule(@RequestParam String scheduleId, @RequestParam String status) {
        if (ScheduleStatusEnum.ENABLED.name().equals(status)) {
            scheduleService.enable(scheduleId);
        } else if (ScheduleStatusEnum.DISABLED.name().equals(status)) {
            scheduleService.disable(scheduleId);
        }
        return Response.sucResp("操作成功！");
    }


    @PostMapping("delete")
    public Response<String> delete(@RequestParam String scheduleId) {
        scheduleService.delete(scheduleId);
        return Response.sucResp("删除成功！");
    }

    @GetMapping("list")
    public PageResponse<List<Schedule>> list(@ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                             @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return scheduleService.list(page, pageSize);
    }


}
