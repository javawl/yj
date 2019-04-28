package com.yj.util;

import com.yj.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisPoolUtil {


    /**
     * 重新设置有效期，单位是秒
     * @param key    key
     * @param exTime 过期时间
     * @return   0 or 1
     */
    public static Long expire(String key, int exTime) {
        Jedis jedis = null;
        Long result = null;
        try{
            //从连接池拿
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, exTime);
        }catch (Exception e){

            //放回坏的里面
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 存入Redis，单位是秒
     * @param key    key
     * @param value  value
     * @param exTime 过期时间
     * @return
     */
    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = null;
        String result = null;
        try{
            //从连接池拿
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, exTime, value);
        }catch (Exception e){

            //放回坏的里面
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 存入Redis，单位是秒
     * @param key    key
     * @param value  value
     * @return
     */
    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try{
            //从连接池拿
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        }catch (Exception e){
            //放回坏的里面
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        }catch (Exception e){
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        }catch (Exception e){
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }
}
