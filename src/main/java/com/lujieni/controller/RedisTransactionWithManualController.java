package com.lujieni.controller;

import com.lujieni.service.TransactionServiceWithAuto;
import com.lujieni.service.TransactionServiceWithManual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther ljn
 * @Date 2019/11/26
 */
@RestController
@RequestMapping("/manual")
public class RedisTransactionWithManualController {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private TransactionServiceWithManual transactionService;

    @RequestMapping("/test1")
    public void test1() {
        transactionService.jiBenShiWu();
        System.out.println("ok");
    }

    @RequestMapping("/test2")
    public void test2() {
        transactionService.shiWuReturnNull();
        System.out.println("ok");
    }

    @RequestMapping("/test3")
    public void test3() {
        transactionService.incompletableTransaction();
        System.out.println("ok");
    }

    @RequestMapping("/test4")
    public void test4() {
        transactionService.useWatch();
        System.out.println("ok");
    }
}
