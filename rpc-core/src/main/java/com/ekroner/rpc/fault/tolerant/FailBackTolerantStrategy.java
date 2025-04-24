package com.ekroner.rpc.fault.tolerant;

import com.ekroner.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 降级到其他服务策略
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy{

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.warn("FailBackTolerantStrategy doTolerant", e);
        return null;
    }
}
