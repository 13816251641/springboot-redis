package com.lujieni.bloomfilter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther ljn
 * @Date 2019/11/26
 * 测试布隆过滤器
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestBloomFilter {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Test
    public void test(){
        /**
         * 2^32=4294967296
         * 9223372036854775807 --> Long.MAX_VALUE
         */
        int a = 3;
        int b = 6;
        System.out.println(a % b);
        System.out.println(Long.MAX_VALUE);
    }

    /**
     * 往bitmap中设置值
     */
    @Test
    public void setValue2Bitmap(){
        set("hello");
    }

    @Test
    public void checkIfExists(){
        if(get("hello")){
            System.out.println("可能存在");
        }else{
            System.out.println("一定不存在");
        }
    }

    private void set(String code) {
        /*
         * 1.对code进行hash运算后在除以2^32取模
         * 2.存入bitmap中即可
         */
        double pow = Math.pow(2, 32);
        /* Math.abs得到绝对值,因为hashcode会返回负数 */
        long index=(long)Math.abs(code.hashCode() % pow);
        redisTemplate.opsForValue().setBit("bloom", index, true);
    }


    private Boolean get(String code) {
        /*
         * 1.对code进行hash运算后在除以2^32取模
         * 2.存入bitmap中即可
         */
        double pow = Math.pow(2, 32);//2^32
        /* Math.abs得到绝对值,因为hashcode会返回负数 */
        long index=(long)Math.abs(code.hashCode() % pow);
        return redisTemplate.opsForValue().getBit("bloom", index);
    }







}
