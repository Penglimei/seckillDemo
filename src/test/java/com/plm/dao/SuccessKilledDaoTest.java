package com.plm.dao;

import com.plm.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        long id = 1001L;
        long phone = 12345678901L;
        int insertNum = successKilledDao.insertSuccessKilled(id,phone);
        System.out.println("insertNum = "+insertNum);
    }

    @Test
    public void findByIdWithSeckill() {
        long id = 1001L;
        long phone = 12345678901L;
        SuccessKilled successKilled = successKilledDao.findByIdWithSeckill(id,phone);
        System.out.println(successKilled);
        System.out.println("=========");
        System.out.println(successKilled.getSeckill());
    }
}