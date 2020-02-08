package com.lujieni.controller;

import com.lujieni.service.impl.TransactionServiceWithAuto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther ljn
 * @Date 2019/11/26
 */
@RestController
@RequestMapping("/auto")
public class RedisTransactionWithAutoController {

    @Autowired
    private TransactionServiceWithAuto transactionServiceWithAuto;

    @RequestMapping("/test1")
    public void test1() {
        transactionServiceWithAuto.test1();
        System.out.println("ok");
    }

    @RequestMapping("/test2")
    public void test2() {
        transactionServiceWithAuto.test2();
        System.out.println("ok");
    }

    @RequestMapping("/test3")
    public void test3() {
        /*
           使用@Transactional的话如果是由于redis内部命令失败的话
           try catch无法捕获到异常,ok仍旧会打印
         */
        try {
            transactionServiceWithAuto.test3();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ok");
    }

    @RequestMapping("/test4")
    public void test4() {
        transactionServiceWithAuto.test4();
        System.out.println("ok");
    }
}
