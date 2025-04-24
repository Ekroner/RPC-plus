package com.ekroner.rpc.fault.tolerant;

import com.ekroner.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {

    /**
     * 容错处理
     *
     * @param context
     * @param e
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
