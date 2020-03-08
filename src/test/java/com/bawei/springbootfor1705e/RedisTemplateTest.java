package com.bawei.springbootfor1705e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate redisTemplate;

    //列出所有的key
    @Test
    public void testKeys() {
        Set<String> keys = redisTemplate.keys("*");
        for(String key : keys) {
            System.out.println(key);
        }
    }

    //设置key的过期时间
    @Test
    public void testExpire() {
      redisTemplate.expire("requirepass",60000, TimeUnit.MILLISECONDS);
    }

    //获取key的过期时间
    @Test
    public void testGetExpire() {
        Long orderSum = redisTemplate.getExpire("orderSum");
        System.out.println(orderSum);
    }

    //查看Key是否存在
    @Test
    public void testKeyExist() {
        Boolean flag = redisTemplate.hasKey("orderSum");
        System.out.println(flag);
    }

    //存入普通keyvalue结构
    @Test
    public void testKeyValuePut(){
        redisTemplate.opsForValue().set("jiangshi","zhangchaobing");
    }

    //存入普通keyvalue结构并设置过期时间
    @Test
    public void testKeyValueWithExpirePut() {
        redisTemplate.opsForValue().set("foo","bar",1L,TimeUnit.MINUTES);
    }

    //读取普通keyvalue结构
    @Test
    public void testGetKeyValue(){
        Object jiangshi = redisTemplate.opsForValue().get("jiangshi");
        System.out.println((String) jiangshi);
    }

    //递增
    @Test
    public void testIncrValue() {
        redisTemplate.opsForValue().increment("testIncr",3L);
    }

    //递减
    @Test
    public void testDecr() {
        redisTemplate.opsForValue().decrement("testIncr",2L);
    }


    //=====================================hash=================================
    //redis中hash结构的key值不能重复
    //向hash结构中put数据
    @Test
    public void testHashCreate() {
        Map<String,Double> map = new HashMap<>();
        map.put("zhangchaobing",77.0);
        map.put("huangbing",88.0);
        redisTemplate.opsForHash().putAll("stu_score",map);
    }

    //获取hash中全部的值
    @Test
    public void testGetHashAll() {
        Set<String> keys = redisTemplate.opsForHash().keys("stu_score");
        for(String key:keys) {
            System.out.println(key);
        }
    }

    //获取hash结构中的数据
    @Test
    public void testGetHash() {
        Object score = redisTemplate.opsForHash().get("stu_score", "zhangchaobing");
        System.out.println((Double) score);
    }

    //删除hash结构中的数据
    @Test
    public void testDelHash() {
        Long num = redisTemplate.opsForHash().delete("stu_score", "zhangchaobing");
        System.out.println(num);
    }

    //递增hash中value的值
    @Test
    public void testIncrHashValue() {
        redisTemplate.opsForHash().increment("stu_score","huangbing",10.0);
    }

    //递减hash结构中的value
    @Test
    public void testDescHashvalue() {
        redisTemplate.opsForHash().increment("stu_score","huangbing",-20);
    }
    //================================================================


    //==================================set===================================
    //redis中set结构的值不能重复
    //向set结构中put数据
    @Test
    public void testPutSet() {
        redisTemplate.opsForSet().add("1705e","wangshuo","zhangrunze","liuning");
    }

    //获取set结构中的值
    @Test
    public void testGetSet() {
        Set<String> members = redisTemplate.opsForSet().members("1705e");
        for(String member:members){
            System.out.println(member);
        }
    }

    //查看set中值是否存在
    @Test
    public void testSetValueExist() {
        Boolean flag = redisTemplate.opsForSet().isMember("1705e", "zhangrunze");
        System.out.println(flag);
    }

    //获取set的长度
    @Test
    public void testGetSetLength() {
        Long size = redisTemplate.opsForSet().size("1705e");
        System.out.println(size);
    }

    //删除set结构的值
    @Test
    public void testDelSetValue(){
        Long nums = redisTemplate.opsForSet().remove("1705e", "zhangrunze");
        System.out.println(nums);
    }

    //=============================================================================

    //==============================list========================================
    //redis中redis的值可以重复
    //向list结构中push数据
    @Test
    public void testPutList() {
        redisTemplate.opsForList().leftPushAll("bigdata","hadoop","spark","flink");
    }

    //根据索引获取list中单条数据
    @Test
    public void testGetPutSingleValue() {
        Object value = redisTemplate.opsForList().index("bigdata", 1);
        System.out.println((String) value);
    }

    @Test
    public void testGetListLength() {
        Long length = redisTemplate.opsForList().size("bigdata");
        System.out.println(length);
    }

    //获取list中全部数据
    @Test
    public void testGetListRange(){
        List<String> values = redisTemplate.opsForList().range("bigdata", 0, -1);
        for(String value : values){
            System.out.println(value);
        }
    }

    //删除list中N个value的值
    @Test
    public void testDelListValue() {
        redisTemplate.opsForList().remove("bigdata",1,"spark");
    }

    //根据索引修改list结构中的数据
    @Test
    public void testUpdateListValue() {
        redisTemplate.opsForList().set("bigdata",0,"storm");
    }
}
