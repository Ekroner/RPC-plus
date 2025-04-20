package com.ekroner.rpc.server.tcp;

import com.ekroner.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData) {
        return "Hello, Client!".getBytes();
    }

    @Override
    public void doStart(int port) {

        Vertx vertx = Vertx.vertx();

        NetServer server = vertx.createNetServer();

        server.connectHandler(socket -> {
           socket.handler(buffer -> {
               byte[] requestData = buffer.getBytes();

               byte[] responseData = handleRequest(requestData);

               socket.write(Buffer.buffer(responseData));
           });
        });

        server.listen(port, result -> {
           if(result.succeeded()) {
               System.out.println("TCP server started on port " + port);
           }
           else {
               System.err.println("Failed to start TCP server: " + result.cause());
           }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
