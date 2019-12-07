package com.lujieni.controller;

import com.lujieni.service.impl.RedisTransactionWithDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther ljn
 * @Date 2019/12/7
 */
@RestController
@RequestMapping("/db")
public class RedisTransactionWithDBController {
    @Autowired
    private RedisTransactionWithDBService redisTransactionWithDBService;

    @RequestMapping("/test1")
    public void test1(){
        try {
            /* redis自身命令导致的异常无法被外界捕获 */
            redisTransactionWithDBService.test1();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ok");
    }

    @RequestMapping("/test2")
    public void test2(){
        try {
            /* 同层java代码导致的异常能被捕获  */
            redisTransactionWithDBService.test2();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ok");
    }

    @RequestMapping("/test3")
    public void test3(){
        try {
            /* 操作数据库导致的异常能被外界代码捕获  */
            redisTransactionWithDBService.test3();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ok");
    }

}
