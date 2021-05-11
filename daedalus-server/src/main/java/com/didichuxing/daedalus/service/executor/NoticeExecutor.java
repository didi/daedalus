package com.didichuxing.daedalus.service.executor;

import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.step.NoticeStepEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Component
@Slf4j
public class NoticeExecutor extends Executor<NoticeStepEntity> {


    @Autowired
    private JavaMailSender mailSender;

    @Override
    protected void exec(PipelineContext pipelineContext, NoticeStepEntity step) {

        if (CollectionUtils.isEmpty(step.getReceivers())) {
            appendLog(pipelineContext, step, "通知接收人为空！");
            return;
        }
        switch (step.getNoticeType()) {
            case EMAIL:
                sendEmail(step.getReceivers(), step.getNoticeContent());
                break;
            default:
                break;
        }

        createResponse(pipelineContext, step, "通知发送成功！");
    }

    private void sendEmail(List<String> receivers, String noticeContent) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 设置收件人，寄件人
        simpleMailMessage.setTo(receivers.toArray(new String[0]));
        simpleMailMessage.setFrom("Daedalus");
        simpleMailMessage.setSubject("Daedalus邮件通知");
        simpleMailMessage.setText(noticeContent);
        // 发送邮件
        mailSender.send(simpleMailMessage);


    }


    @Override
    public StepTypeEnum getStepType() {
        return StepTypeEnum.NOTICE;
    }


}
