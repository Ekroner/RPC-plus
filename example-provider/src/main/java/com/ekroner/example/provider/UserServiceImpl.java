package com.ekroner.example.provider;

import com.ekroner.example.common.service.UserService;
import com.ekroner.example.common.model.User;

public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
