package com.ekroner.rpc.fault.tolerant;

import com.ekroner.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理策略
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("静默处理异常", e);
        return new RpcResponse();
    }
}
