package com.plm.dao;

import com.plm.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKilledDao {

    /**
     * 添加秒杀成功明细，可以通过联合主键过滤同一用户重复秒杀同一商品
     * @param seckillId
     * @param userPhone
     * @return 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);

    /**
     *  根据 seckillId查询秒杀成功的明细，以及所秒杀商品的信息
     * @param seckillId
     * @return
     */
    SuccessKilled findByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);
}
