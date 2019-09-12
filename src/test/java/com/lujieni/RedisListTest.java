package com.lujieni;


import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisListTest {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    private ListOperations<Object, Object> listOperations;

    @Before
    public void before(){
        listOperations= redisTemplate.opsForList();
    }





}
