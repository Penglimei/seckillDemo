package com.plm.dao;

import com.plm.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {

    /**
     *  减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数 > 1，表示更新的记录行数
     */
    int reduceNumber(@Param("seckillId")long seckillId, @Param("killTime")Date killTime);

    /**
     *  根据 seckillId 查询秒杀的产品对象
     * @param seckillId
     * @return
     */
    Seckill findById(long seckillId);


    /**
     *  查询秒杀产品列表
     *
     */

    List<Seckill> getAllSeckill();

    /**
     * 使用存储过程执行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String,Object> paramMap);

}
