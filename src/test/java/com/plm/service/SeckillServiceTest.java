package com.plm.service;

import com.plm.dto.Exposer;
import com.plm.dto.SeckillExecution;
import com.plm.entity.Seckill;
import com.plm.exception.RepeatKillException;
import com.plm.exception.SeckillCloseException;
import com.plm.exception.SeckillException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list = {}",list);
    }

    @Test
    public void getById() {
        Seckill seckill = seckillService.getById(1000L);
        logger.info("seckill = {}",seckill);
    }

    @Test
    public void exportSeckillUrlAndexecuteSeckill() {
        long seckillId = 1000L;
        long userPhone = 12345678901L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            logger.info("exposer = {}",exposer);
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId,
                        userPhone,exposer.getMd5());
                logger.info("seckillExecution = {}",seckillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else {
            // 秒杀未开启
            logger.warn("exposer = {}",exposer);
        }
    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId= 1001;
        long phone = 1234565362;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
            logger.info(execution.getStateInfo());
        }
    }

}