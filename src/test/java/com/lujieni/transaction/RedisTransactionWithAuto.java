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


    /**
     * 即使加了@Transactional标签,如果redisTemplate中的
     * enableTransactionSupport为false,事务仍旧不生效
     */
    @Test
    @Transactional
    public void hello(){
        redisTemplate.opsForValue().set("av","b");
        redisTemplate.opsForValue().increment("d");
    }



}
