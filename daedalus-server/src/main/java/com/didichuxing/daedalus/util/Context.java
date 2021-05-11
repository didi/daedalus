package com.didichuxing.daedalus.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.didichuxing.daedalus.common.dto.User;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;

/**
 * @author : jiangxinyu
 * @date : 2020/4/29
 */
public class Context {

    private static final TransmittableThreadLocal<User> CURRENT_USER = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<ExecuteRequest> OFFLINE_REQUEST = new TransmittableThreadLocal<>();

    public static void setUser(User user) {
//        if (user.getUsername() == null || user.getUsernameCN() == null) {
//            user.setUsername("jiangxinyu");
//            user.setUsernameCN("姜信宇");
//        }
        CURRENT_USER.set(user);
    }

    public static User getUser() {
        return CURRENT_USER.get();
    }

    public static ExecuteRequest getRequest() {
        return OFFLINE_REQUEST.get();
    }

    public static void setRequest(ExecuteRequest request) {
        OFFLINE_REQUEST.set(request);
    }

    public static void clear() {
        CURRENT_USER.remove();
        OFFLINE_REQUEST.remove();
    }
}
