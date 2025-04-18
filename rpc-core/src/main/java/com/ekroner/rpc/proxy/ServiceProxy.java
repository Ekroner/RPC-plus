package com.ekroner.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.config.RpcConfig;
import com.ekroner.rpc.constant.RpcConstant;
import com.ekroner.rpc.model.RpcRequest;
import com.ekroner.rpc.model.RpcResponse;
import com.ekroner.rpc.model.ServiceMetaInfo;
import com.ekroner.rpc.registry.Registry;
import com.ekroner.rpc.registry.RegistryFactory;
import com.ekroner.rpc.serializer.JdkSerializer;
import com.ekroner.rpc.serializer.Serializer;
import com.ekroner.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

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

      try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
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
