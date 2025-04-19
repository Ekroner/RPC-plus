package com.ekroner.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.model.RpcRequest;
import com.ekroner.rpc.model.RpcResponse;
import com.ekroner.rpc.serializer.JdkSerializer;
import com.ekroner.rpc.serializer.Serializer;
import com.ekroner.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理(JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {

  /**
   * 调用代理
   *
   * @param proxy
   * @param method
   * @param args
   * @return
   * @throws Throwable
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    RpcRequest rpcRequest = RpcRequest.builder()
        .serviceName(method.getDeclaringClass().getName())
        .methodName(method.getName())
        .parameterTypes(method.getParameterTypes())
        .args(args)
        .build();
    try {
      byte[] bodyBytes = serializer.serialize(rpcRequest);
      try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
            .body(bodyBytes)
            .execute()) {
        byte[] result = httpResponse.bodyBytes();
        RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
        return rpcResponse.getData();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
