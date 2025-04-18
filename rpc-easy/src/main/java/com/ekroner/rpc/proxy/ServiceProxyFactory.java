package com.ekroner.rpc.proxy;

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
    return (T) Proxy.newProxyInstance(
        serviceClass.getClassLoader(),
        new Class[] {serviceClass},
        new ServiceProxy());
  }
}
