package com.lujieni.redislock;

import com.lujieni.util.RedisLockUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试自己封装的redis全局锁
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRedisLock {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;


    @Autowired
    private DefaultRedisScript<Long> drs;

    /**
     * 测试加锁代码
     * 这里要注意:如果加锁成功返回true;加锁失败返回null!!!
     */
    @Test
    public void testGetReidsLock(){
        String lock="lock";
        String requestId="jiang";
        Boolean result = RedisLockUtil.getRedisLock(redisTemplate, lock, requestId, 120);
        System.out.println(result);
    }

    /**
     * 测试解锁代码
     * 解锁失败返回0 成功返回1
     */
    @Test
    public void testReleaseReidsLock(){
        String lock="lock";
        String requestId="jiang";
        Long result = RedisLockUtil.releaseRedisLock(redisTemplate,drs,lock,requestId);
        System.out.println(result);
    }








}
