package com.ekroner.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.config.RpcConfig;
import com.ekroner.rpc.constant.RpcConstant;
import com.ekroner.rpc.fault.retry.RetryStrategy;
import com.ekroner.rpc.fault.retry.RetryStrategyFactory;
import com.ekroner.rpc.fault.tolerant.TolerantStrategy;
import com.ekroner.rpc.fault.tolerant.TolerantStrategyFactory;
import com.ekroner.rpc.loadbalancer.LoadBalancer;
import com.ekroner.rpc.loadbalancer.LoadBalancerFactory;
import com.ekroner.rpc.model.RpcRequest;
import com.ekroner.rpc.model.RpcResponse;
import com.ekroner.rpc.model.ServiceMetaInfo;
import com.ekroner.rpc.registry.Registry;
import com.ekroner.rpc.registry.RegistryFactory;
import com.ekroner.rpc.serializer.Serializer;
import com.ekroner.rpc.serializer.SerializerFactory;
import com.ekroner.rpc.server.tcp.VertxTcpClient;
import com.ekroner.rpc.server.tcp.VertxTcpServer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
    Map<String, Object> requestParams = new HashMap<>();
    requestParams.put("methodName", rpcRequest.getMethodName());
    ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

    RpcResponse rpcResponse;
    try {
      RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
      rpcResponse = retryStrategy.doRetry(() ->
        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
      );
    } catch (Exception e) {
      TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
      rpcResponse = tolerantStrategy.doTolerant(null, e);
    }
    return rpcResponse.getData();
  }
}
