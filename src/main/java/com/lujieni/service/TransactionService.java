package com.lujieni.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther ljn
 * @Date 2019/11/27
 */
@Service
public class TransactionService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Transactional
    public void test1() {
        /*
            程序代码出错,redis的set操作的确会回滚,
            a和b都不会被赋值
         */
        redisTemplate.opsForValue().set("a", 520);
        redisTemplate.opsForValue().set("b", 250);
        throw new RuntimeException();
    }

    @Transactional
    public void test2() {
        /*
           incre操作redis会失败,但b的赋值操作成功
         */
        redisTemplate.opsForValue().increment("d");
        redisTemplate.opsForValue().set("b", 250);
    }

    public void test3() {
        /*
           执行完increment语句后直接抛RedisCommandExecutionException异常,
           因为抛了异常后程序就停止运行了,b的辅助操作不会执行。
         */
        redisTemplate.opsForValue().increment("d");
        redisTemplate.opsForValue().set("b", 5);
    }
}
