package com.lujieni.transaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther ljn
 * @Date 2019/11/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTransactionWithAuto {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Test
    @Transactional
    public void hello(){
        redisTemplate.opsForValue().set("a","b111111");
        throw  new RuntimeException();
    }



}
