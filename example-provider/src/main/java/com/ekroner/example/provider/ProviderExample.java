package com.ekroner.example.provider;

import com.ekroner.example.common.service.UserService;
import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.registry.LocalRegistry;
import com.ekroner.rpc.server.HttpServer;
import com.ekroner.rpc.server.VertxHttpServer;

/**
 * 生产者示例
 */
public class ProviderExample {
  public static void main(String[] args) {
    RpcApplication.init();

    LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

    HttpServer httpServer = new VertxHttpServer();
    httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
  }
}
