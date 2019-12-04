package com.lujieni.controller;

import com.lujieni.service.TransactionServiceWithMyOwn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther ljn
 * @Date 2019/12/4
 *
 *
 */
@RestController
@RequestMapping("/own")
public class RedisTransactionWithMyOwnController {

    @Autowired
    private TransactionServiceWithMyOwn transactionServiceWithMyOwn;

    @RequestMapping("/test1")
    public void test3() {
        transactionServiceWithMyOwn.test1();
        System.out.println("ok");
    }
}
