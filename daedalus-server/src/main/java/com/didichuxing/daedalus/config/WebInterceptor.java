package com.didichuxing.daedalus.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.daedalus.common.dto.User;
import com.didichuxing.daedalus.pojo.Constants;
import com.didichuxing.daedalus.util.Context;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

/**
 * @author : jiangxinyu
 * @date : 2020/4/29
 */
@Component
public class WebInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String username = request.getHeader(Constants.USERNAME);
        String usernamezh = request.getHeader(Constants.USERNAMEZH);
        String cookie = request.getHeader(Constants.COOKIE);
        String usernameCN = decode(usernamezh);
        Cookie[] cookies = request.getCookies();

        User user = new User();
        user.setUsername(username);
        user.setUsernameCN(usernameCN);
        user.setCookie(cookie);
        if (cookies != null) {
            Arrays.stream(cookies).filter(ck -> Constants.INNER_USER_INFO.equals(ck.getName())).findFirst()
                    .ifPresent(ck -> {
                        String userInfo = decode(ck.getValue());
                        JSONObject userInfoObj = JSON.parseObject(userInfo);
                        user.setEmail(userInfoObj.getString(Constants.EMAIL_PREFIX) + Constants.DIDIGLOBAL_COM);
                        user.setMobile(userInfoObj.getString(Constants.MOBILE));
                    });
        }

        Context.setUser(user);
        MDC.put("username", username);
        return super.preHandle(request, response, handler);
    }

    private String decode(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        try {
            return URLDecoder.decode(str, Constants.UTF_8);
        } catch (UnsupportedEncodingException e) {

        }
        return str;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Context.clear();
        MDC.clear();
    }
}
