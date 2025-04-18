package com.ekroner.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 */
public class LocalRegistry {

      /**
       * 注册信息存储
       */
      private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

      /**
       * 注册服务
       * @param serviceName 服务名
       * @param impClass 服务类
       */
      public static void register(String serviceName, Class<?> impClass) {
        map.put(serviceName, impClass);
      }

      /**
       * 获取服务
       * @param serviceName 服务名
       * @return 服务类
       */
      public static Class<?> get(String serviceName) {
        return map.get(serviceName);
      }

      /**
       * 删除服务
       * @param serviceName 服务名
       */
      public static void remove(String serviceName) {
        map.remove(serviceName);
      }
}
