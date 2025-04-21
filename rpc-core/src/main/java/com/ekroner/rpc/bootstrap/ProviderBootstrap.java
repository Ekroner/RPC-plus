package com.ekroner.rpc.bootstrap;

import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.config.RegistryConfig;
import com.ekroner.rpc.config.RpcConfig;
import com.ekroner.rpc.model.ServiceMetaInfo;
import com.ekroner.rpc.model.ServiceRegisterInfo;
import com.ekroner.rpc.registry.LocalRegistry;
import com.ekroner.rpc.registry.Registry;
import com.ekroner.rpc.registry.RegistryFactory;
import com.ekroner.rpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者初始化
 */
public class ProviderBootstrap {

    /**
     * 初始化
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        //初始化RPC配置
        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();

            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "注册失败", e);
            }
        }

        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
