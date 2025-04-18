package com.ekroner.rpc.serializer;

import com.ekroner.rpc.model.RpcRequest;
import com.ekroner.rpc.model.RpcResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * JSON序列化器
 */
public class JsonSerializer implements Serializer{
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  public <T> byte[] serialize(T obj) throws IOException {
    return OBJECT_MAPPER.writeValueAsBytes(obj);
  }

  @Override
  public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
    T obj = OBJECT_MAPPER.readValue(bytes, classType);
    if (obj instanceof RpcRequest) {
      return handleRequest((RpcRequest) obj, classType);
    }
    if (obj instanceof RpcResponse) {
      return handleResponse((RpcResponse) obj, classType);
    }
    return obj;
  }

  /**
   * 处理RpcRequest
   * @param rpcRequest
   * @param type
   * @param {@link T}
   * @return
   * @throws IOException
   */
  private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
    Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
    Object[] args = rpcRequest.getArgs();

    for (int i = 0; i < parameterTypes.length; i++) {
      Class<?> clazz = parameterTypes[i];
      if (!clazz.isAssignableFrom(args[i].getClass())) {
        byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
        args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
      }
    }

    return type.cast(rpcRequest);
  }

  /**
   * 处理RpcResponse
   * @param rpcResponse
   * @param type
   * @return {@link T}
   * @throws IOException
   */
  private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
    byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
    rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes, rpcResponse.getDataType()));
    return type.cast(rpcResponse);
  }
}
