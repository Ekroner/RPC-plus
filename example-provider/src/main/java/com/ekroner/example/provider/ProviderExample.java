package com.ekroner.example.provider;

import com.ekroner.example.common.service.UserService;
import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.config.RegistryConfig;
import com.ekroner.rpc.config.RpcConfig;
import com.ekroner.rpc.constant.RpcConstant;
import com.ekroner.rpc.model.ServiceMetaInfo;
import com.ekroner.rpc.registry.Registry;
import com.ekroner.rpc.registry.LocalRegistry;
import com.ekroner.rpc.registry.RegistryFactory;
import com.ekroner.rpc.server.HttpServer;
import com.ekroner.rpc.server.VertxHttpServer;

/**
 * 服务提供者示例
 */
public class ProviderExample {
  public static void main(String[] args) {
    //RPC 初始化
    RpcApplication.init();

    //注册服务
    String serviceName = UserService.class.getName();
    LocalRegistry.register(serviceName, UserServiceImpl.class);

    //注册服务到注册中心
    RpcConfig rpcConfig = RpcApplication.getRpcConfig();
    RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
    Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
    ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName(serviceName);
    serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
    serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
    serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
    try {
      registry.register(serviceMetaInfo);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    HttpServer httpServer = new VertxHttpServer();
    httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
  }
}
