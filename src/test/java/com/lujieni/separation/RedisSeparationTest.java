package com.lujieni.separation;

import com.lujieni.entity.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试redis的读写分离
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisSeparationTest {
    /*
       @Autowired先根据类型装配,如果符合条件的大于1个,再根据字段名称装配
     */
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private RedisTemplate<Object,Object> redisSlaveTemplate;

    @Test
    public void testMaster(){
        Student student = new Student();
        student.setName("张三");
        student.setGender("男");
        redisTemplate.opsForValue().set("student",student);
    }

    @Test
    public void testSlave(){
        Student student =(Student) redisSlaveTemplate.opsForValue().get("student");
        System.out.println(student);
    }
}
