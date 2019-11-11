package com.lujieni.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisLockUtil {

    /**
     * 尝试获取分布式锁
     * @param redisTemplate redis工具类
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static Boolean getRedisLock(RedisTemplate<Object,Object> redisTemplate, String lockKey, String requestId, int expireTime) {
         return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 尝试释放分布式锁
     * @param redisTemplate redis工具类
     * @param drs lua脚本对象
     * @param lockKey 锁
     * @param requestId 请求标识
     */
    public static Long releaseRedisLock(RedisTemplate<Object, Object> redisTemplate, DefaultRedisScript<Long> drs, String lockKey, String requestId) {
        List<Object> parameters = new ArrayList<>();//入参
        parameters.add(lockKey);
        return redisTemplate.execute(drs, parameters, requestId);
    }

}
