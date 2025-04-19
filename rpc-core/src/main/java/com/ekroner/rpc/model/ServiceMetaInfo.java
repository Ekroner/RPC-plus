package com.ekroner.rpc.model;

/**
 * 服务元信息
 */
public class ServiceMetaInfo {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion = "1.0";

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口
     */
    private int servicePort;

    /**
     * 服务分组（暂未实现)
     */
    private String serviceGroup = "default";

    /**
     * 获取服务键名
     *
     * @return
     */
    public String getServiceKey() {
        // 后续可扩展服务分组
        // return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务注册节点域名
     *
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s:%s:%s", getServiceKey(), serviceHost, servicePort);
    }
}
