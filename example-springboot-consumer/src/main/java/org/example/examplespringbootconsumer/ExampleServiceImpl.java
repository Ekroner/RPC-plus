package org.example.examplespringbootconsumer;

import com.ekroner.example.common.model.User;
import com.ekroner.example.common.service.UserService;
import org.example.rpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("ekroner");
        User newUser = userService.getUser(user);
        System.out.println(newUser.getName());
    }
}
