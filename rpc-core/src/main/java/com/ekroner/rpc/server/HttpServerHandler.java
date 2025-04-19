package com.ekroner.rpc.server;

import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.model.RpcRequest;
import com.ekroner.rpc.model.RpcResponse;
import com.ekroner.rpc.registry.LocalRegistry;
import com.ekroner.rpc.serializer.Serializer;
import com.ekroner.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.lang.reflect.Method;
import java.io.IOException;

public class HttpServerHandler implements Handler<HttpServerRequest> {

  @Override
  public void handle(HttpServerRequest request) {
    final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    System.out.println("Received request: " + request.method() + " " + request.uri());

    request.bodyHandler(body -> {
      byte[] bytes = body.getBytes();
      RpcRequest rpcRequest = null;
      try {
        rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
      } catch (Exception e) {
        e.printStackTrace();
      }

      RpcResponse rpcResponse = new RpcResponse();
      if(rpcResponse == null) {
        rpcResponse.setMessage("rpcRequest is null");
        doResponse(request, rpcResponse, serializer);
        return;
      }

      try {
        Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
        Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
        rpcResponse.setData(result);
        rpcResponse.setDataType(method.getReturnType());
        rpcResponse.setMessage("ok");
      } catch (Exception e) {
        e.printStackTrace();
        rpcResponse.setMessage(e.getMessage());
        rpcResponse.setException(e);
      }

      doResponse(request, rpcResponse, serializer);
    });
  }

  /**
   * 响应
   *
   * @param request
   * @param rpcResponse
   * @param serializer
   */
  private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
    HttpServerResponse httpServerResponse = request.response()
            .putHeader("content-type", "application/json");
    try {
      byte[] serialized = serializer.serialize(rpcResponse);
      httpServerResponse.end(Buffer.buffer(serialized));
    } catch (IOException e) {
      e.printStackTrace();
      httpServerResponse.end(Buffer.buffer());
    }
  }
}
