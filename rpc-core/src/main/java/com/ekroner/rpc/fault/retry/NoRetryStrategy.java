package com.ekroner.rpc.fault.retry;

import com.ekroner.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 不重试策略
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy{

    /**
     * 不重试
     *
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
