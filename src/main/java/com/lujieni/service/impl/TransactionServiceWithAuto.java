package com.lujieni.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther ljn
 * @Date 2019/11/27
 */
@Service
public class TransactionServiceWithAuto {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Transactional
    public void test1() {
        /*
            外部程序代码导致的出错,因为加了@Transactional,
            redis的set操作的确会回滚,a和b都不会被赋值
         */
        /*
            日志:
            Invoke 'multi' on bound conneciton
            Invoke 'set' on bound conneciton
            Invoke 'set' on bound conneciton
            Invoke 'discard' on bound conneciton
            Invoke 'close' on bound conneciton
         */
        redisTemplate.opsForValue().set("a", 1);
        redisTemplate.opsForValue().set("b", 2);
        throw new RuntimeException();
    }

    public void test2() {
        /*
            外部程序代码导致的出错,没有加@Transactional,
            redis的set操作不会回滚,a和b都会被赋值。
            日志只会打印为:Opening RedisConnection
         */

        /*
           1.当enableTransactionSupport为false的时候,每次调用完
             redis的命令后RedisConnectionUtils.releaseConnection(conn, factory)
             方法中的!isConnectionTransactional(conn, factory)方法被触发,
             conn.close();命令得到执行,将连接归还给连接池

           2.当enableTransactionSupport为true且事务为手工事务不使用
             @Transactional标签时,RedisConnectionUtils.releaseConnection(conn, factory)
             方法中的命令一条都不会执行,连接不会释放,这在springboot2.0之前会导致
             连接数耗尽,在2.0之后因为使用lettuce客户端得以复用连接而没事!!!

           3.当enableTransactionSupport为true且事务使用注解事务,即方法上添加
             @Transactional标签时,连接会释放,是被RedisTransactionSynchronizer
             类中的afterCompletion方法来释放
         */
        redisTemplate.opsForValue().set("a", 1);
        redisTemplate.opsForValue().set("b", 2);
        throw new RuntimeException();
    }

    @Transactional
    public void test3() {
        /*
           在注解事务中incre操作会失败,
           但b的赋值操作成功,不会回滚!!!
         */
        /*
           日志
           Opening RedisConnection
           Invoke 'multi' on bound conneciton
           Invoke 'incr' on bound conneciton
           Invoke 'set' on bound conneciton
           Invoke 'get' on unbound conneciton
           Invoke 'exec' on bound conneciton
           Invoke 'close' on bound conneciton
         */
        redisTemplate.opsForValue().increment("d");//假如它失败
        redisTemplate.opsForValue().set("b", 250);//b的赋值操作不会回滚
        /* 能直接得到值是因为给你开了一个新connection */
        Integer b = (Integer)redisTemplate.opsForValue().get("b");
        System.out.println(b);
    }

    public void test4() {
        /*
           执行完increment语句后直接抛RedisCommandExecutionException异常,
           因为抛了异常后程序就停止运行了,b的赋值操作不会执行。
         */

        /*
           日志:Opening RedisConnection
         */
        redisTemplate.opsForValue().increment("d");
        redisTemplate.opsForValue().set("b", 5);
    }

}
