package com.lujieni.controller;

import com.lujieni.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther ljn
 * @Date 2019/11/26
 */
@RestController
public class RedisTransactionWithAutoController {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping("/test1")
    public void test1() {
        transactionService.test1();
        System.out.println("ok");
    }

    @RequestMapping("/test2")
    public void test2() {
        transactionService.test2();
        System.out.println("ok");
    }

    @RequestMapping("/test3")
    public void test3() {
        transactionService.test3();
        System.out.println("ok");
    }

    @RequestMapping("/test4")
    public void test4() {
        transactionService.test4();
        System.out.println("ok");
    }

}
