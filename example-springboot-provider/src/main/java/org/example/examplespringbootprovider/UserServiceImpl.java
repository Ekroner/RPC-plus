package org.example.examplespringbootprovider;

import com.ekroner.example.common.model.User;
import com.ekroner.example.common.service.UserService;
import org.example.rpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
