package com.ekroner.rpc.server;

import io.vertx.core.Vertx;

/**
 * Vertx HTTP 服务器
 */
public class VertxHttpServer implements HttpServer{

    /**
     * 启动Vertx HTTP 服务器
     * @param port 端口
     */
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();

        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        server.requestHandler(new HttpServerHandler());

        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.out.println("Failed to start server: " + result.cause());
            }
        });
    }
}
