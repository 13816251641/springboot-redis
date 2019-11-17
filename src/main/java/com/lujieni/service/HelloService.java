package com.lujieni.service;

import com.lujieni.annotation.RedisLock;
import org.springframework.stereotype.Service;

/**
 * @Auther ljn
 * @Date 2019/11/11
 */
@Service
public class HelloService {
    @RedisLock
    public String sayHelloMethod(String content){
        System.out.println("sayHelloMethod内部逻辑执行");
        return  content;
    }
}
