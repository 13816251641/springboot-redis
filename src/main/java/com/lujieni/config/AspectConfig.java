package com.lujieni.config;

import com.lujieni.annotation.RedisLock;
import com.lujieni.util.RedisLockService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Auther ljn
 * @Date 2019/11/11
 */
@Aspect
@Configuration
@Slf4j
public class AspectConfig {
    @Autowired
    private RedisLockService redisLockService;

    @Pointcut("@annotation(com.lujieni.annotation.RedisLock)")
    private void lockPoint(){
    }

    @Around(value = "lockPoint()")
    public Object doAroundAdvice(ProceedingJoinPoint pjp){
        /*
            log.info(">>>>>>>>>>Around");
            log.info("环绕通知的目标方法名："+pjp.getSignature().getName());
        */
        String uuid = UUID.randomUUID().toString();
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        if(redisLockService.getRedisLock(redisLock.key(),uuid,redisLock.expire())){
            try {
                return pjp.proceed();
            } catch (Throwable throwable) {
                log.error(throwable.getMessage());
            }finally {
                redisLockService.releaseRedisLock(redisLock.key(),uuid);
            }
            return null;
        }else {
            return null;
        }


    }
}
