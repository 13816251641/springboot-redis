package com.lujieni.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisStringTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> opsForValue;


    @Before
    public void before(){
        opsForValue=stringRedisTemplate.opsForValue();
    }

    @Test
    public void  testSet(){
        //删除健（每次测试前我都会对当前测试的键进行删除，防止影响测试结果）
        stringRedisTemplate.delete("liu1");
        opsForValue.set("liu1", "liu1");
        System.out.println(opsForValue.get("liu1"));//liu1
    }

    @Test
    public void  testSetTimeOut() throws InterruptedException{
        stringRedisTemplate.delete("liu2");
        //加了失效机制
        opsForValue.set("liu2", "liu2", 10, TimeUnit.SECONDS);
        Thread.sleep(5000);
        System.out.println(opsForValue.get("liu2"));//liu2
        Thread.sleep(5000);
        System.out.println(opsForValue.get("liu2"));//null
    }

    @Test
    public void  testSetOverwrite(){
        stringRedisTemplate.delete("liu3");
        opsForValue.set("liu3", "liu3");
        System.out.println(opsForValue.get("liu3"));//liu3
        //该方法是用 value 参数覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始
        opsForValue.set("liu3", "666666", 2);
        System.out.println(opsForValue.get("liu3"));//li666666
    }

    /**
     * 如果存在就不设置
     */
    @Test
    public void  testSetIfAbsent(){
        stringRedisTemplate.delete("liu4");
        stringRedisTemplate.delete("liu5");
        opsForValue.set("liu4", "liu4");
        System.out.println(opsForValue.setIfAbsent("liu4", "liu4"));//false
        System.out.println(opsForValue.setIfAbsent("liu5", "liu5"));//true
    }

    @Test
    public void  testMultiSetAndGet (){
        stringRedisTemplate.delete("liu6");
        stringRedisTemplate.delete("liu7");
        stringRedisTemplate.delete("liu8");
        stringRedisTemplate.delete("liu9");
        Map<String,String> param = new HashMap<>();
        param.put("liu6", "liu6");
        param.put("liu7", "liu7");
        param.put("liu8", "liu8");
        //为多个键分别设置它们的值
        opsForValue.multiSet(param);
        List<String> keys = new ArrayList<>();
        keys.add("liu6");
        keys.add("liu7");
        keys.add("liu8");
        //为多个键分别取出它们的值
        List<String> results = opsForValue.multiGet(keys);
        for (String result : results) {
            System.out.println(result);
            /*
                liu6
                liu7
                liu8
             */
        }
        param.clear();
        param.put("liu8", "hahaha");
        param.put("liu9", "liu9");
        /*为多个键分别设置它们的值，如果存在则返回false，不存在返回true*/
        System.out.println(opsForValue.multiSetIfAbsent(param));//false liu8,liu9都不存在才会赋值
        System.out.println(opsForValue.get("liu8"));//liu8
        System.out.println(opsForValue.get("liu9"));//liu9:null
    }

    /**
     * 设置键的值并返回其旧值
     */
    @Test
    public void  testGetAndSet(){
        stringRedisTemplate.delete("liu9");
        opsForValue.set("liu9", "liu9");
        System.out.println(opsForValue.getAndSet("liu9", "haha"));//liu9<-旧值
        System.out.println(opsForValue.get("liu9"));//haha
    }

    @Test
    public void  testIncrement(){
        stringRedisTemplate.delete("liu10");
        opsForValue.set("liu10", "6");
        //值增长，支持整形和浮点型
        System.out.println(opsForValue.increment("liu10", 1));//7
        System.out.println(opsForValue.increment("liu10", 1.1));//8.1
        opsForValue.set("liu10", "liu10");
        /*
           org.springframework.data.redis.RedisSystemException: Error in execution;
           nested exception is io.lettuce.core.RedisCommandExecutionException: ERR value is not an integer or out of range
         */
        opsForValue.increment("liu10", 1);
    }

    @Test
    public void  testAppend(){
        stringRedisTemplate.delete("liu11");
        stringRedisTemplate.delete("liu12");
        /*
          如果key已经存在并且是一个字符串，则该命令将该值追加到字符串的末尾。
          如果键不存在，则它被创建并设置为空字符串，因此APPEND在这种特殊情况下将类似于SET。
         */
        opsForValue.append("liu11", "liu11");
        System.out.println(opsForValue.get("liu11"));//liu11
        opsForValue.set("liu12", "liu12");
        opsForValue.append("liu12", "haha");
        System.out.println(opsForValue.get("liu12"));//liu12haha
    }

    @Test
    public void  testGetPart(){
        stringRedisTemplate.delete("liu13");
        opsForValue.set("liu13", "liu13");
        //截取key所对应的value字符串
        System.out.println(opsForValue.get("liu13", 0, 2));//liu
    }

    @Test
    public void  testSize(){
        stringRedisTemplate.delete("liu14");
        opsForValue.set("liu14", "liu14");
        //返回key所对应的value值得长度
        System.out.println(opsForValue.size("liu14"));//5
    }

    /*
      这个不会 哈哈~
     */
    @Test
    public void  testSetBit(){
        stringRedisTemplate.delete("liu15");
        //true为1，false为0
        opsForValue.set("liu15", "liu15");
        //对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)
        //key键对应的值value对应的ASCII码,在offset的位置(从左向右数)变为value
        System.out.println(opsForValue.setBit("liu15", 13, true));//false
        System.out.println(opsForValue.get("liu15"));//lmu15
        for(int i = 0 ; i<"liu15".length()*8;i++){
            if(opsForValue.getBit("liu15", i)){
                System.out.print(1);
            }else{
                System.out.print(0);
            }
            //0110110001101101011101010011000100110101
        }
    }


}
