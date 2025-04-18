package com.ekroner.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理（JDK动态代理）
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

  /**
   * 调用代理
   *
   * @return
   * @throws Throwable
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Class<?> methodReturnType = method.getReturnType();
    log.info("mock invoke {}", method.getName());
    return getDefaultObject(methodReturnType);
  }

  /**
   * 生成指定类型的默认值对象
   *
   * @param type
   * @return
   */
  private Object getDefaultObject(Class<?> type) {
    if (type.isPrimitive()) {
      if (type == int.class) {
        return 0;
      } else if (type == short.class) {
        return (short) 0;
      } else if (type == long.class) {
        return 0L;
      } else if (type == float.class) {
        return 0F;
      } else if (type == double.class) {
        return 0D;
      } else if (type == boolean.class) {
        return false;
      } else if (type == char.class) {
        return '\u0000';
      }
    }
    return null;
  }
}
