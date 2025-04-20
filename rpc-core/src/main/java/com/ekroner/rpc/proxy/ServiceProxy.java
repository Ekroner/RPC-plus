package com.ekroner.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.config.RpcConfig;
import com.ekroner.rpc.constant.RpcConstant;
import com.ekroner.rpc.model.RpcRequest;
import com.ekroner.rpc.model.RpcResponse;
import com.ekroner.rpc.model.ServiceMetaInfo;
import com.ekroner.rpc.protocol.*;
import com.ekroner.rpc.registry.Registry;
import com.ekroner.rpc.registry.RegistryFactory;
import com.ekroner.rpc.serializer.JdkSerializer;
import com.ekroner.rpc.serializer.Serializer;
import com.ekroner.rpc.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    String serviceName = method.getDeclaringClass().getName();
    RpcRequest rpcRequest = RpcRequest.builder()
            .serviceName(method.getDeclaringClass().getName())
            .methodName(method.getName())
            .parameterTypes(method.getParameterTypes())
            .args(args)
            .build();
    try {
      byte[] bodyBytes = serializer.serialize(rpcRequest);

      //从注册中心中获取服务提供者请求地址
      RpcConfig rpcConfig = RpcApplication.getRpcConfig();
      Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
      ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
      serviceMetaInfo.setServiceName(serviceName);
      serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
      List<ServiceMetaInfo> serviceMetaInfoList = registry.seriveDiscovery(serviceMetaInfo.getServiceKey());
      if (CollUtil.isEmpty(serviceMetaInfoList)) {
        throw new RuntimeException("暂无服务地址");
      }

      ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

      Vertx vertx = Vertx.vertx();
      NetClient netClient = vertx.createNetClient();
      CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
      netClient.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(), result -> {
        if(result.succeeded()) {
          System.out.println("Connected to TCP server");
          NetSocket socket = result.result();

          ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
          ProtocolMessage.Header header = new ProtocolMessage.Header();
          header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
          header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
          header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
          header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
          header.setRequestId(IdUtil.getSnowflakeNextId());
          protocolMessage.setHeader(header);
          protocolMessage.setBody(rpcRequest);

          try {
            Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
            socket.write(encodeBuffer);
          } catch (IOException e) {
            throw new RuntimeException("协议消息编码错误");
          }

          socket.handler(buffer -> {
            try {
              ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
              responseFuture.complete(rpcResponseProtocolMessage.getBody());
            } catch (IOException e) {
              throw new RuntimeException("协议消息解码错误");
            }
          });
        }
        else {
          System.err.println("Failed to connect to TCP server");
        }
      });

      RpcResponse rpcResponse = responseFuture.get();
      netClient.close();
      return rpcResponse.getData();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
