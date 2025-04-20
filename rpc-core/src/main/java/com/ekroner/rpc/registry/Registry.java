package com.ekroner.rpc.registry;

import com.ekroner.rpc.config.RegistryConfig;
import com.ekroner.rpc.model.ServiceMetaInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 注册中心
 */
public interface Registry {

    /**
     * 初始化
     * @param registryConfig 注册中心配置
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务
     * @param serviceMetaInfo 服务元信息
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务
     * @param serviceMetaInfo 服务元信息
     */
    void unregister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     * @param serviceKey 服务键名
     * @return 服务元信息List
     */
    List<ServiceMetaInfo> seriveDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 心跳检测
     */
    void heartBeat();

    /**
     * 本机注册的节点key集合
     */
    final Set<String> localRegisterNodeKeySet = new HashSet<>();
}
