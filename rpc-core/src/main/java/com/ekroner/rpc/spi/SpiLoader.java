package com.ekroner.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.ekroner.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI加载器
 */
@Slf4j
public class SpiLoader {

  /**
   * 存储已加载的类
   */
  private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

  /**
   * 对象实例缓存
   */
  private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

  /**
   * 系统SPI目录
   */
  private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

  /**
   * 用户自定义SPI目录
   */
  private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

  /**
   * 扫描路径
   */
  private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

  /**
   * 动态加载的类列表
   */
  private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

  /**
   * 加载所有类型
   */
  public static void loadAll() {
    log.info("加载所有SPI");
    for(Class<?> clazz : LOAD_CLASS_LIST) {
      load(clazz);
    }
  }

  /**
   * 获取某个接口的实例
   * @param clazz
   * @param key
   * @param <T>
   * @return
   */
  public static <T> T getInstance(Class<T> clazz, String key) {
    String className = clazz.getName();
    Map<String, Class<?>> keyClassMap = loaderMap.get(className);
    if(keyClassMap == null) {
      throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", className));
    }
    if(!keyClassMap.containsKey(key)) {
      throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", className, key));
    }

    Class<?> implClass = keyClassMap.get(key);
    String implClassName = implClass.getName();
    if(!instanceCache.containsKey(implClassName)) {
      try {
        instanceCache.put(implClassName, implClass.newInstance());
      } catch (InstantiationException | IllegalAccessException e) {
        String errorMsg = String.format("%s 类实例化失败", implClassName);
        throw new RuntimeException(errorMsg, e);
      }
    }
    return (T) instanceCache.get(implClassName);
  }

  /**
   * 加载某个类型
   * @param loadClass
   * @throw IOException
   */
  public static Map<String, Class<?>> load(Class<?> loadClass) {
    log.info("加载SPI {}", loadClass.getName());

    Map<String, Class<?>> keyClassMap = new HashMap<>();
    for(String scanDir : SCAN_DIRS) {
      List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
      for(URL resource : resources) {
        try {
          InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
          BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
          String line;
          while((line = bufferedReader.readLine()) != null) {
            String[] split = line.split("=");
            if(split.length > 1) {
              String key = split[0];
              String className = split[1];
              keyClassMap.put(key, Class.forName(className));
            }
          }
        } catch (Exception e) {
          log.error("spi resource load error", e);
        }
      }
    }
    loaderMap.put(loadClass.getName(), keyClassMap);
    return keyClassMap;
  }
}
