package com.ekroner.rpc.model;

import com.ekroner.rpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.io.Serializable;

/**
 * RPC请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable{

  /**
   * 服务名称
   */
  private String serviceName;

  /**
   * 方法名称
   */
  private String methodName;

  /**
   * 服务版本
   */
  private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

  /**
   * 参数类型列表
   */
  private Class<?>[] parameterTypes;

  /**
   * 参数列表
   */
  private Object[] args;
}
