package com.ekroner.example.consumer;

import com.ekroner.example.common.model.User;
import com.ekroner.example.common.service.UserService;
import com.ekroner.rpc.proxy.ServiceProxyFactory;

/**
 * 消费者示例
 */
public class ConsumerExample {

  public static void main(String[] args) {
    UserService userService = ServiceProxyFactory.getProxy(UserService.class);
    User user = new User();
    user.setName("ekroner");
    User newUser = userService.getUser(user);
    if(newUser != null) {
      System.out.println(newUser.getName());
    }
    else {
      System.out.println("User == null");
    }
    long number = userService.getNumber();
    System.out.println(number);
  }
}
