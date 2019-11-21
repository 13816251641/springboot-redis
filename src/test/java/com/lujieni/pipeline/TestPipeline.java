package com.lujieni.pipeline;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lujieni.entity.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Auther ljn
 * @Date 2019/11/21
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPipeline {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    /**
     * 使用管段设置数据
     */
    @Test
    public void usePiplelineWrite(){
        List<Object> objects = redisTemplate.executePipelined(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                /* connection使用的还是lettuce连接 */
                connection.set("c".getBytes(), serializeObject(new Student().setName("张三").setGender("女")));
                connection.set("d".getBytes(), serializeObject(new Student().setName("张三").setGender("女")));
                /* 返回值必须返回为null */
                return null;
            }
        });
        System.out.println(objects);
    }


    /**
     * 使用管道读数据,反序列化方式使用redisTemplate配置的,这里是jackson
     */
    @Test
    public void usePiplelineRead(){
        List<Object> objects = redisTemplate.executePipelined(new RedisCallback<Student>() {
            @Override
            public Student doInRedis(RedisConnection connection) throws DataAccessException {
                /* connection使用的还是lettuce连接 */
                connection.get("a".getBytes());
                connection.get("b".getBytes());
                connection.get("c".getBytes());
                connection.get("d".getBytes());
                /* connection.closePipeline()不能调用 */
                //List<Object> result = connection.closePipeline();
                return null;
            }
        });
        System.out.println(objects);
    }

    /*
       使用jackson序列化对象
     */
    private byte[] serializeObject(Object obj){
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
                Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer.serialize(obj);
    }
}
