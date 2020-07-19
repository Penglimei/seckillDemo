package com.plm.dao.cache;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.plm.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis缓存数据
 *
 *  import java.io.Serializable;是源生JDK序列化
 *
 *      protostuff 序列化相比 Serializable序列化，
 *      可将占有空间压缩到原先的1/10～1/5，
 *      且压缩速度比原先的快两个数量级，更节省CPU。
 */
public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JedisPool jedisPool;
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    // 构造函数注入
    public RedisDao(String ip,int port) {
        jedisPool = new JedisPool(ip,port);
    }

    public Seckill getSeckill(long seckillId){
        // redis 操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckillId;
                // redis,jedis并没有实现内部序列化操作，因此在存取对象时针对二进制数组操作
                // get->byte[]->反序列化->Object(Seckill)
                // 采用自定义的序列化
                byte[] bytes = jedis.get(key.getBytes());
                // 从缓存中获取到seckillId对应的字节数组
                if(bytes != null){
                    // 空对象
                    Seckill seckill = schema.newMessage();
                    // 用 protostuff 通过schema将bytes传到seckill
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    // seckill 反序列化
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill){
        // set Object(Seckill)->序列化->byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                // 超时缓存
                int timeout = 60*60; //1小时
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
