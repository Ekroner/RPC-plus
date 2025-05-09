package com.ekroner.rpc.registry;

import com.ekroner.rpc.spi.SpiLoader;

/**
 * 注册中心工厂
 */
public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取注册中心实例
     *
     * @param key 注册中心key
     * @return
     */
    public static Registry getInstance(String key)  {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
