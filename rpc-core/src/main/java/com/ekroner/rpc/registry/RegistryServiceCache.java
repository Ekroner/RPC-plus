package com.ekroner.rpc.registry;

import com.ekroner.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心本地服务缓存
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写缓存
     *
     * @param serviceKey 服务key
     * @param newServiceCache 更新后的缓存
     * @return
     */
    void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache.put(serviceKey, newServiceCache);
    }

    /**
     * 读缓存
     *
     * @param serviceKey 服务key
     * @return
     */
    List<ServiceMetaInfo> readCache(String serviceKey) {
        return this.serviceCache.get(serviceKey);
    }

    /**
     * 清除缓存
     */
    void clearCache(String serviceKey) {
        this.serviceCache.remove(serviceKey);
    }
}
