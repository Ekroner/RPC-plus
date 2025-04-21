package com.ekroner.example.provider;

import com.ekroner.example.common.service.UserService;
import com.ekroner.rpc.bootstrap.ProviderBootstrap;
import com.ekroner.rpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者示例
 */
public class ProviderExample {
  public static void main(String[] args) {
    List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
    ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
    serviceRegisterInfoList.add(serviceRegisterInfo);

    ProviderBootstrap.init(serviceRegisterInfoList);
  }
}
