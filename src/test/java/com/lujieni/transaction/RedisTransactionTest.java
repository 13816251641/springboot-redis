package com.lujieni.transaction;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 测试redis的事务能力
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTransactionTest {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    /**
     * 基本事务操作
     */
    @Test
    public void jiBenShiWu(){
        /*
            开启事务支持,不设置为true的话会导致Lettuce连接超时,
            原因还不知道,但为false的话每次获取的都是新的连接肯
            定会有问题的!!!
            这样写isActualNonReadonlyTransactionActive为false,不会
            生成connection的代理对象也不会自动帮你multi,只会帮你将
            连接绑定到当前线程上!!!
         */
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();
        redisTemplate.opsForValue().set("num", 200);
        redisTemplate.exec();
    }

    /**
     * 事务中任何get操作都会返回null,因为事务不是即时的,
     * 返回值都在exec中
     */
    @Test
    public void shiWuReturnNull(){
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();//开启事务
        redisTemplate.opsForValue().increment("incr");
        Integer incr = (Integer)redisTemplate.opsForValue().get("incr");
        System.out.println(incr);//null
        redisTemplate.opsForValue().set("name", "name");
        List<Object> exec = redisTemplate.exec();
        System.out.println(exec);
    }


    /**
     * 在事务中试图对字母进行附加,执行exce方法时直接抛
     * 运行时异常,但后面合法的命令仍然会执行,因此redis
     * 的事务是不完全的
     */
    @Test
    public void incompletableTransaction(){
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();//开启事务
        /* 假如incr的值是"abc",对"abc"自增会报错 */
        redisTemplate.opsForValue().increment("incr");
        /* 这条语句仍然会执行,不会回滚 */
        redisTemplate.opsForValue().set("name", "name");
        /* 执行会抛异常 */
        List<Object> exec = redisTemplate.exec();
        System.out.println(exec);
    }

    /**
     * 使用watch乐观锁
     *
     * 在watch之后exec之前一旦num字段被别的session改变过,
     * 那么exec的时候,会返回一个size为0的list但不会报错,
     * 并且当中的命令即使都是合法的也不会执行!!!
     */
    @Test
    public void useWatch(){
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.watch("num");
        redisTemplate.multi();
        redisTemplate.opsForValue().set("age",25);
        redisTemplate.opsForValue().set("num", 1200);
        List<Object> exec = redisTemplate.exec();
        System.out.println(exec.size());// []
    }
}
