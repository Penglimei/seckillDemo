package com.plm.dao;

import com.plm.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

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
     *  根据偏移量offset 查询秒杀产品列表
     * @param offset 偏移量
     * @param limit 从偏移量往后数要获取多少个产品的信息
     * @return
     *
     *  由于offset、limit两个参数传入方法时是形参arg0,arg1，
     *  SeckillDao.xml中获取两个参数时并不知道哪一个是谁，
     *  因此需要使用 MyBatis的注解@Param() 让SeckillDao.xml中获取两个参数时
     *  知道哪一个是offset，哪一个是limit
     *  @Param("offset")int offset, @Param("limit")int limit
     */
    List<Seckill> findAll(@Param("offset")int offset, @Param("limit")int limit);
}
