package com.lujieni.controller;

import com.lujieni.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StringRedisController {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @GetMapping("/test")
    public void test(){
        Student student = new Student();
        student.setGender("男");
        redisTemplate.opsForValue().set("zhusisi",student);

        redisTemplate.opsForValue().set("zzzz","ccccc");
    }

}
