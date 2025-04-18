package com.ekroner.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian序列化器
 */
public class HessianSerializer implements Serializer {
  @Override
  public <T> byte[] serialize(T obj) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
    hessianOutput.writeObject(obj);
    return byteArrayOutputStream.toByteArray();
  }

  @Override
  public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    HessianInput hessianInput = new HessianInput(byteArrayInputStream);
    return (T) hessianInput.readObject(classType);
  }
}
