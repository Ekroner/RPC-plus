package com.ekroner.rpc.fault.tolerant;

/**
 * 容错策略键名常量
 */
public interface TolerantStrategyKeys {

    /**
     * 容错策略：快速失败
     */
    String FAIL_FAST = "failFast";

    /**
     * 容错策略：故障转移
     */
    String FAIL_OVER = "failOver";

    /**
     * 容错策略：静默处理
     */
    String FAIL_SAFE = "failSafe";

    /**
     * 容错策略：失败回滚
     */
    String FAIL_BACK = "failBack";
}
