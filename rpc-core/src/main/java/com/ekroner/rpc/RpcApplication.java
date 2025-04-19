package com.ekroner.rpc;

import com.ekroner.rpc.config.RegistryConfig;
import com.ekroner.rpc.config.RpcConfig;
import com.ekroner.rpc.constant.RpcConstant;
import com.ekroner.rpc.registry.Registry;
import com.ekroner.rpc.registry.RegistryFactory;
import com.ekroner.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用
 */
@Slf4j
public class RpcApplication {

  private static volatile RpcConfig rpcConfig;

  /**
   * 初始化RPC框架
   *
   * @param newRpcConfig 自定义配置
   */
  public static void init(RpcConfig newRpcConfig) {
    rpcConfig = newRpcConfig;
    log.info("rpc init, config = {}", newRpcConfig.toString());
    // 初始化注册中心
    RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
    Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
    registry.init(registryConfig);
    log.info("registry init, config = {}", registryConfig);
  }

  /**
   * 初始化
   */
  public static void init() {
    RpcConfig newRpcConfig;
    try {
      newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
    } catch (Exception e) {
      newRpcConfig = new RpcConfig();
    }
    init(newRpcConfig);
  }

  /**
   * 获取RPC框架配置
   *
   * @return
   */
  public static RpcConfig getRpcConfig() {
    if(rpcConfig == null) {
      synchronized (RpcApplication.class) {
        if(rpcConfig == null) {
          init();
        }
      }
    }
    return rpcConfig;
  }
}
