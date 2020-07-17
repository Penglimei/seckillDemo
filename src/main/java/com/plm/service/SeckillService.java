package com.plm.service;


import com.plm.dto.Exposer;
import com.plm.dto.SeckillExecution;
import com.plm.entity.Seckill;
import com.plm.exception.RepeatKillException;
import com.plm.exception.SeckillCloseException;
import com.plm.exception.SeckillException;

import java.util.List;

/**
 *  业务接口
 */
public interface SeckillService {

    /**
     *  查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     *  查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     *  秒杀开启时输出秒杀接口地址，
     *  否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     *  执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
            throws SeckillException,RepeatKillException, SeckillCloseException;
}
