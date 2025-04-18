package com.ekroner.example.provider;

import com.ekroner.example.common.service.UserService;
import com.ekroner.rpc.registry.LocalRegistry;
import com.ekroner.rpc.server.HttpServer;
import com.ekroner.rpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
      LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

      HttpServer httpServer = new VertxHttpServer();
      httpServer.doStart(8080);
    }
}
