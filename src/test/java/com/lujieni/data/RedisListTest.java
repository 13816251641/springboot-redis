package com.lujieni.data;


import com.lujieni.entity.Student;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisListTest {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    private ListOperations<Object, Object> opsForList;

    @Before
    public void before(){
        opsForList= redisTemplate.opsForList();
    }

    @Test
    public void  testLeftPush(){
        redisTemplate.delete("li1");
        //先进后出(左进左出)
        opsForList.leftPush("li1", "a");
        opsForList.leftPush("li1", "b");
        opsForList.leftPush("li1", "c");
        List<Object> range = opsForList.range("li1", 0, -1);
        for (Object object : range) {
            System.out.println(object);//c b a
        }
        opsForList.leftPush("li1", "d");
        opsForList.leftPush("li1", "e");
        opsForList.leftPush("li1", "f");
        List<Object> range1 = opsForList.range("li1", 0, -1);
        for (Object object : range1) {
            System.out.println(object);//f e d c b a
        }
    }

    @Test
    public void testTrim(){
        redisTemplate.delete("li2");
        opsForList.leftPush("li2", "a");
        opsForList.leftPush("li2", "b");
        opsForList.leftPush("li2", "c");
        opsForList.trim("li2", 1, -1);
        List<Object> range = opsForList.range("li2", 0, -1);
        for (Object object : range) {
            System.out.println(object);//b a
        }
    }

    @Test
    public void testSize(){
        redisTemplate.delete("li3");
        System.out.println(opsForList.size("li3"));//0
        opsForList.leftPush("li3", "a");
        System.out.println(opsForList.size("li3"));//1
    }

    @Test
    public void testLeftPushAll(){
        redisTemplate.delete("li4");
        opsForList.leftPushAll("li4", "a","b","c");
        System.out.println(opsForList.size("li4"));//3
        List<Object> param = new ArrayList<Object>();
        param.add("d");
        param.add("e");
        param.add("f");
        opsForList.leftPushAll("li4", param);
        System.out.println(opsForList.size("li4"));//6
        opsForList.leftPushAll("li4", new Object[]{"g","h"});
        System.out.println(opsForList.size("li4"));//8
        List<Object> range = opsForList.range("li4", 0, -1);
        for (Object object : range) {
            System.out.println(object);//h g f e d c b a
        }
        opsForList.leftPush("li4", param);
        System.out.println(opsForList.size("li4"));//9
        opsForList.leftPush("li4", new Object[]{"g","h"});
        System.out.println(opsForList.size("li4"));//10
        List<Object> range1 = opsForList.range("li4", 0, -1);
        for (Object object : range1) {
            System.out.println(object);
            /*
                [Ljava.lang.Object;@6d06654b
                [d, e, f]
                h
                g
                f
                e
                d
                c
                b
                a
             */
        }
    }

    @Test
    public void testLeftPushAllCollection(){
        redisTemplate.delete("li5");
        List<Object> strs = new ArrayList<Object>();
        strs.add("a");
        strs.add("b");
        strs.add("c");
        opsForList.leftPushAll("li5", strs);
        List<Object> range = opsForList.range("li5", 0, -1);
        for (Object object : range) {
            System.out.println(object);//c b a
        }
    }

    @Test
    public void testLeftPushIfPresent(){
        redisTemplate.delete("li6");
        opsForList.leftPushIfPresent("li6", "a");
        System.out.println(opsForList.size("li6"));//0
        opsForList.leftPush("li6", "a");
        opsForList.leftPushIfPresent("li6", "b");
        System.out.println(opsForList.size("li6"));//2
    }

    @Test
    public void testRightPush(){
        redisTemplate.delete("li7");
        //先进先出(右进左出)
        opsForList.rightPush("li7", "a");
        opsForList.rightPush("li7", "b");
        opsForList.rightPush("li7", "c");
        List<Object> range = opsForList.range("li7", 0, -1);
        for (Object object : range) {
            System.out.println(object);//a b c
        }
    }

    @Test
    public void testRightPushAll(){
        redisTemplate.delete("li8");
        opsForList.rightPushAll("li8", "a","b","c");
        List<Object> range = opsForList.range("li8", 0, -1);
        for (Object object : range) {
            System.out.println(object);//a b c
        }
    }

    @Test
    public void testRightPushAllCollection(){
        redisTemplate.delete("li9");
        List<Object> param = new ArrayList<Object>();
        param.add("a");
        param.add("b");
        param.add("c");
        opsForList.rightPushAll("li9", param);
        List<Object> range = opsForList.range("li9", 0, -1);
        for (Object object : range) {
            System.out.println(object);//a b c
        }
    }

    @Test
    public void testRightPushIfPresent(){
        redisTemplate.delete("li10");
        opsForList.rightPushIfPresent("li10", "a");
        System.out.println(opsForList.size("li10"));//0
        opsForList.rightPush("li10", "a");
        opsForList.rightPushIfPresent("li10", "b");
        System.out.println(opsForList.size("li10"));//2
    }

    @Test
    public void testRightPushPivot(){
        redisTemplate.delete("li11");
        //把value值放到key对应列表中pivot值的右面，如果pivot值存在的话
        opsForList.rightPush("li11", "a","b");
        System.out.println(opsForList.size("li11"));//0
        opsForList.rightPush("li11", "a");
        opsForList.rightPush("li11", "a","b");
        List<Object> range = opsForList.range("li11", 0, -1);
        for (Object object : range) {
            System.out.println(object);//a b
        }
    }

    @Test
    public void testSetThrowExc1(){
        redisTemplate.delete("li12");
        opsForList.rightPush("li12", "a");
        opsForList.set("li12", 2, "h");//ERR INDEX OUT OF RANGE
    }

    @Test
    public void testSetThrowExc2(){
        redisTemplate.delete("li13");
        opsForList.set("li13", 2, "a");//ERR NO SUCH KEY
    }

    @Test
    public void testSet(){
        redisTemplate.delete("li14");
        opsForList.rightPush("li14", "a");
        System.out.println(opsForList.range("li14", 0, -1));//[a]
        opsForList.set("li14", 0, "b");
        System.out.println(opsForList.range("li14", 0, -1));//[b]
    }

    @Test
    public void testRemove(){
        redisTemplate.delete("li15");
        opsForList.rightPush("li15", "a");
        opsForList.rightPush("li15", "b");
        opsForList.rightPush("li15", "c");
        opsForList.rightPush("li15", "b");
        opsForList.rightPush("li15", "a");
        opsForList.remove("li15", 1, "b");//将删除列表中存储在列表中第一次出现的"b"
        List<Object> list = opsForList.range("li15", 0, -1);
        for (Object object : list) {
            System.out.println(object);//a c b a
        }
    }

    @Test
    public void testIndex(){
        redisTemplate.delete("li16");
        opsForList.rightPush("li16", "a");
        opsForList.rightPush("li16", 1);
        opsForList.rightPush("li16", new Student());
        opsForList.rightPush("li16", "c");
        opsForList.rightPush("li16", "b");
        Object index = opsForList.index("li16", 2);
        System.out.println(index);//结果：Student [name=null, gender=null]
    }

    @Test
    public void testLeftPop(){
        redisTemplate.delete("li17");
        opsForList.rightPush("li17", 1);
        opsForList.rightPush("li17", 2);
        opsForList.rightPush("li17", 3);
        opsForList.leftPop("li17");
        List<Object> range = opsForList.range("li17", 0, -1);
        for (Object object : range) {
            System.out.println(object);//结果：2 3
        }
    }

    @Test
    public void testLeftPopTimeout(){
        //弹出队列最左边的元素，如果没有则保持线程，等有了再弹出，设有超时时间，当过了超时时间，则线程终止
        opsForList.leftPop("li18",5, TimeUnit.SECONDS);
    }

    @Test
    public void testRightPopAndLeftPush(){
        //从一个队列的右边弹出一个元素并将这个元素放入另一个指定队列的最左边
        redisTemplate.delete("li18");
        redisTemplate.delete("li19");
        opsForList.rightPushAll("li18", "a","b","c");
        opsForList.rightPopAndLeftPush("li18", "li19");
        List<Object> range = opsForList.range("li18", 0, -1);
        for (Object object : range) {
            System.out.println(object);//结果：a b
        }
        List<Object> range1 = opsForList.range("li19", 0, -1);
        for (Object object : range1) {
            System.out.println(object);//结果：c
        }
    }









}
