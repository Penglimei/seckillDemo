package com.plm.service.impl;

import com.plm.dao.SeckillDao;
import com.plm.dao.SuccessKilledDao;
import com.plm.dao.cache.RedisDao;
import com.plm.dto.Exposer;
import com.plm.dto.SeckillExecution;
import com.plm.entity.Seckill;
import com.plm.entity.SuccessKilled;
import com.plm.enums.SeckillStatEnum;
import com.plm.exception.RepeatKillException;
import com.plm.exception.SeckillCloseException;
import com.plm.exception.SeckillException;
import com.plm.service.SeckillService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    // 注入service依赖
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    // md5盐 ，随便写越复杂越好，用于混淆要加密的值，不易被破解
    private final String slat = "fgvgsdbdHIOUBIYOfwgfw4&*()&^guybl";
    @Autowired
    private RedisDao redisDao;

    @Override
    public List<Seckill> getSeckillList() {
        List<Seckill> seckills = seckillDao.findAll(0, 4);
        return seckills;
    }

    @Override
    public Seckill getById(long seckillId) {
        Seckill seckill = seckillDao.findById(seckillId);
        return seckill;
    }

    /**
     * 使用注解控制事务方法的有点：
     * 1、开发团队达成一致约定，统一标注事务方法的编程风格，不需要再去查编程文档；
     * 2、保证事务方法的执行时间尽可能短，不要穿插其他的网络操作RPC/HTTP请求；
     * 3、不是所有的方法都需要事务，如：只有一条修改操作，只读操作不需要事务。
     *
     * @param seckillId
     * @return
     */
    @Override
    @Transactional
    public Exposer exportSeckillUrl(long seckillId) {
        // 优化点：缓存优化,建立在超时(1小时)的基础上维护一致性
        // 1、访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            // 2、访问数据库
            seckill = seckillDao.findById(seckillId);
            if (seckill == null){
                return new Exposer(false,seckillId);
            }else {
                // 3、放入redis
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        // 当前系统时间
        Date curTime = new Date();
        // 不能暴露秒杀地址
        if (curTime.getTime() < startTime.getTime() || curTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, curTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 可以暴露秒杀地址
        // 转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    // MD5加密
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        // base转为二进制进而加密
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    // 秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {

        try {
            // 商品不存在或者是重复秒杀
            if (md5 == null || !md5.equals(getMD5(seckillId))) {
                throw new SeckillException("seckill data rewrite");
            }
            Date curTime = new Date();
            // 增加购买明细
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

            if (insertCount <= 0) { // 看是否该明细被重复插入，即用户是否重复秒杀
                // 重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                // 秒杀成功，减库存成功
                int updateCount = seckillDao.reduceNumber(seckillId,curTime);
                if (updateCount <= 0) {
                    // 重复插入秒杀明细
                    throw new RepeatKillException("seckill is closed");
                } else {
                    // 秒杀成功，增加明细成功
                    SuccessKilled successKilled = successKilledDao.findByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (RepeatKillException e1) {
            throw e1;
        } catch (SeckillCloseException e2) {
            throw e2;
        } catch (SeckillException e) { // 回滚
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error : " + e.getMessage());
        }
        /**
         *  在以上代码中，我们捕获了运行时异常，
         *  原因是Spring的事务默认是发生了RuntimeException才会回滚，
         *  发生了其他异常不会回滚，
         *  所以在最后的catch块里通过
         *  throw new SeckillException("seckill inner error :"+e.getMessage());
         *  将编译期异常转化为运行期异常。
         */
    }

    // 秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚----通过存储过程实现
    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5==null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        // 执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map,"result",-2);
            if(result == 1){
                SuccessKilled successKilled = successKilledDao.findByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,successKilled);
            }else {
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }
    }
}
