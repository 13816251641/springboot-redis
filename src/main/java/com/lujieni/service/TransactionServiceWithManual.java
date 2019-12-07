package com.lujieni.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试redis的手工控制事务
 */
@Service
public class TransactionServiceWithManual {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    /**
     * 基本事务操作
     */
    public void jiBenShiWu(){
        /*
            开启事务支持,EnableTransactionSupport不设置为true的
            话会导致lettuce连接超时,原因还不知道,但为false的话
            每次获取的都是新的连接事务肯定没有办法完成!!!
            这样写isActualNonReadonlyTransactionActive为false,不会
            生成connection的代理对象也不会自动帮你multi,只会帮你将
            连接绑定到当前线程上!!!
         */
        /*
           日志打印:Opening RedisConnection
         */
        /*
           当enableTransactionSupport为true且事务为手工事务不使用
           @Transactional标签时,RedisConnectionUtils.releaseConnection(conn, factory)
           方法中的命令一条都不会执行,连接不会释放,这在springboot2.0之前会导致
           连接数耗尽,在2.0之后因为使用lettuce客户端得以复用连接而没事!!!
         */
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();
        redisTemplate.opsForValue().set("num", 200);
        redisTemplate.exec();
    }

    /**
     * 手工事务中任何get操作都会返回null,因为事务不是即时的返回值都在exec中
     */
    public void shiWuReturnNull(){
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
     * 运行时异常,但后面合法的命令仍然会执行!!!,因此
     * redis的事务是不完全的
     */
    public void incompletableTransaction(){
        redisTemplate.multi();//开启事务
        /* 假如c的值是"abc",对"abc"自增会报错 */
        redisTemplate.opsForValue().increment("c");
        /* 这条语句仍然会执行,不会回滚 */
        redisTemplate.opsForValue().set("name", "name");
        /* 下面命令执行会抛运行时异常 */
        List<Object> exec = redisTemplate.exec();
        /* exec不会打印,因为异常了 */
        System.out.println(exec);
    }

    /**
     * 使用watch乐观锁
     *
     * 在watch之后exec之前一旦num字段被别的session改变过,
     * 那么exec的时候,会返回一个size为0的list但不会报错,
     * 并且当中的命令即使都是合法的也不会执行!!!
     */
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
