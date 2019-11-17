package com.lujieni.annotation;

import java.lang.annotation.*;

/**
 * @Auther ljn
 * @Date 2019/11/11
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RedisLock {

    /** 锁的资源，redis的key*/
    String key() default "redis:key";

    /** 过期时间,单位秒*/
    int expire() default 120;

}
