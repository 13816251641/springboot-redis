package com.lujieni.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther ljn
 * @Date 2019/12/4
 */
@Service
public class TransactionServiceWithMyOwn {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    /*
        当enableTransactionSupport为false的时候的确会close连接
        日志:
        Opening RedisConnection
        Closing bound connection.

        redisTemplate.execute方法中会得到连接并将连接绑定到当前线程中,
        之后operations.opsForSet().add代码的连接都是从当前线程中获取的,
        所以事务成立。最后再execute方法中会调用RedisConnectionUtils.unbindConnection(factory);
        关闭连接,关闭连接代表将连接归还给连接池,不意味着切断和redis的连接!!!

     */
    public void test1(){
        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForSet().add("keyNAME", "value1");
                operations.opsForSet().add("aaa","bbb");
                return operations.exec();
            }
        });
        System.out.println(txResults);
    }

}
