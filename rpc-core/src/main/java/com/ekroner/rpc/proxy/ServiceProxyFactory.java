package com.ekroner.rpc.proxy;

import com.ekroner.rpc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 */
public class ServiceProxyFactory {

  /**
   * 根据服务类获取服务代理
   *
   * @param serviceClass
   * @param <T>
   * @return
   */
  public static <T> T getProxy(Class<T> serviceClass) {
    if(RpcApplication.getRpcConfig().isMock()) {
      return getMockProxy(serviceClass);
    }
    return (T) Proxy.newProxyInstance(
        serviceClass.getClassLoader(),
        new Class[] {serviceClass},
        new ServiceProxy());
  }

  /**
   * 根据服务类获取mock代理
   *
   * @param serviceClass
   * @param <T>
   * @return
   */
  public static <T> T getMockProxy(Class<T> serviceClass) {
    return (T) Proxy.newProxyInstance(
        serviceClass.getClassLoader(),
        new Class[] {serviceClass},
        new MockServiceProxy());
  }
}
