package com.lujieni.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Auther ljn
 * @Date 2019/11/11
 */
@Service
public class RedisLockService {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Autowired
    private DefaultRedisScript<Long> drs;
    private static final Long RELEASE_SUCCESS = 1L;
    /**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public Boolean getRedisLock(String lockKey, String requestId, int expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 尝试释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     */
    public boolean releaseRedisLock(String lockKey, String requestId) {
        List<Object> parameters = new ArrayList<>();//入参
        parameters.add(lockKey);
        Long result = redisTemplate.execute(drs, parameters, requestId);
        if(RELEASE_SUCCESS.equals(result)){
            return true;
        }else{
            return false;
        }
    }
}
