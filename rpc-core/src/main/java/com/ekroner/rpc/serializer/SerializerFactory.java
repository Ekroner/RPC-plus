package com.ekroner.rpc.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂
 */
public class SerializerFactory {

  /**
   * 序列化映射
   */
  private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>() {
    {
      put(SerializerKeys.JDK, new JdkSerializer());
      put(SerializerKeys.KRYO, new KryoSerializer());
      put(SerializerKeys.HESSIAN, new HessianSerializer());
      put(SerializerKeys.JSON, new JsonSerializer());
    }
  };

  /**
   * 默认序列化器
   */
  private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get("jdk");

  /**
   * 获取实例
   *
   * @param key
   * @return
   */
  public static Serializer getInstance(String key) {
    return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
  }
}
