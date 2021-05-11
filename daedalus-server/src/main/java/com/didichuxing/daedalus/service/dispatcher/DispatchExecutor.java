package com.didichuxing.daedalus.service.dispatcher;

import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.pojo.request.ExecuteResult;

/**
 * @author : jiangxinyu
 * @date : 2021/1/20
 */
public interface DispatchExecutor {

    ExecuteResult doDispatch(ExecuteRequest request);

}
