package com.lujieni.service.impl;

import com.lujieni.entity.User;
import com.lujieni.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther ljn
 * @Date 2019/12/7
 */
@Service
public class RedisTransactionWithDBService {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Transactional
    public void test1(){
        /*
            redis和数据库在一起使用,如果异常是由于redis的非法命令
            导致的(无论代码前后位置),数据库仍然会插入数据,并且redis
            导致的异常内外部代码使用try catch都无法捕获
         */
        redisTemplate.opsForValue().increment("d");//产生异常
        /* 仍然会插入数据 */
        userService.save(new User().setName("redis").setAge(28));
    }

    @Transactional
    public void test2(){
        /*
           如果异常是由于同层java代码导致的,redis和数据库的
           操作命令都不会执行,异常能被外部代码使用try catch捕获
         */
        redisTemplate.opsForValue().set("jk","jk");
        userService.save(new User().setName("redis22").setAge(28));
        throw new RuntimeException("自定义异常");
    }

    @Transactional
    public void test3(){
        /*
           如果异常是由于操作数据库的命令导致的,
           redis的操作命令不会执行,数据库也会回滚,
           异常能被外界捕获
         */
        /*
           日志:
                 Opening RedisConnection
                 Invoke 'multi' on bound conneciton
                 Invoke 'set' on bound conneciton
                 Invoke 'discard' on bound conneciton
                 Invoke 'close' on bound conneciton
         */
        redisTemplate.opsForValue().set("jk","jk");
        /* 数据库执行报错 */
        userService.save(new User().setName("redis22222222222222").setAge(28));
    }
}
