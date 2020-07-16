package com.plm.dao;

import com.plm.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * SeckillDao 测试类
 *
 *  配置Spring和 junit整合,junit启动时加载springIOC容器
 */
// junit启动时加载springIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉 junit spring配置文件的位置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    // 注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        int updateNum = seckillDao.reduceNumber(1000L,killTime);
        System.out.println("updateNum = "+updateNum);
    }

    @Test
    public void findById() {
        long id = 1000;
        Seckill seckill = seckillDao.findById(id);
        System.out.println(seckill);
    }

    @Test
    public void findAll() {
        List<Seckill> seckills = seckillDao.findAll(0,100);
        for(Seckill seckill : seckills){
            System.out.println(seckill);
        }
    }
}