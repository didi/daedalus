package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.common.dto.User;
import com.didichuxing.daedalus.pojo.Constants;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.util.Context;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户信息挖掘，设置隐藏变量
 *
 * @author : jiangxinyu
 * @date : 2020/9/9
 */
@Component
public class UserDigger implements Digger {

    @Override
    public void dig(PipelineContext pipelineContext) {
        User user = Context.getUser();
        Map<String, String> vars = pipelineContext.getVars();
        vars.put(Constants.SSO_COOKIE, user.getCookie());
        vars.put(Constants.SSO_USER_NAME, user.getUsername());
        vars.put(Constants.SSO_USER_NAME_CN, user.getUsernameCN());
        vars.put(Constants.USER_MOBILE, user.getMobile());
        vars.put(Constants.USER_EMAIL, user.getEmail());
    }
}
