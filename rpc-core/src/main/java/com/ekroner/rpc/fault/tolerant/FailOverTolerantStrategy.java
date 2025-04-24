package com.ekroner.rpc.fault.tolerant;

import com.ekroner.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 故障转移策略
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy{

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("FailOverTolerantStrategy doTolerant", e);
        return null;
    }
}
