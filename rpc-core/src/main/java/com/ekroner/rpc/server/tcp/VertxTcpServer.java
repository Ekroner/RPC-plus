package com.ekroner.rpc.server.tcp;

import com.ekroner.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServer {

    @Override
    public void doStart(int port) {

        Vertx vertx = Vertx.vertx();

        NetServer server = vertx.createNetServer();

        server.connectHandler(new TcpServerHandler());

        server.listen(port, result -> {
           if(result.succeeded()) {
               System.out.println("TCP server started on port " + port);
           }
           else {
               System.err.println("Failed to start TCP server: " + result.cause());
           }
        });
    }
}
