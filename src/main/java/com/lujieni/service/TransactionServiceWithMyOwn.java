package com.lujieni.service;

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
        Opening RedisConnection
        Closing bound connection.
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
