package com.plm.web;


import com.plm.dto.Exposer;
import com.plm.dto.SeckillExecution;
import com.plm.dto.SeckillResult;
import com.plm.entity.Seckill;
import com.plm.enums.SeckillStatEnum;
import com.plm.exception.RepeatKillException;
import com.plm.exception.SeckillCloseException;
import com.plm.exception.SeckillException;
import com.plm.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;


    /**
     *  查看所有秒杀商品
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        // 获取列表
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        //  /WEB-INF/jsp/list.jsp
        return "list";
    }


    /**
     * 查看某一秒杀商品的详细信息
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     *  暴露秒杀地址
     * @param seckillId
     * @return
     */
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody // springmvc看到这个注解时会将 exposer()的返回值封装为json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            // 请求成功
            result = new SeckillResult<>(true,exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            // 请求失败
            result = new SeckillResult<>(false,e.getMessage());
        }
        return result;
    }

    /**
     *  执行秒杀
     * @param seckillId
     * @param phone 从用户浏览器中名为killPhone的cookie中获取的，
     *              但是存在一个问题当浏览器中没有名为killPhone的cookie时，springmvc会报错，
     *              所以将其设为非必须的，即对名为killPhone的cookie的验证逻辑放在程序中处理，而不是直接报错.
     * @param md5
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @CookieValue(value = "killPhone",required = false) Long phone,
                                                   @PathVariable("md5") String md5){
        // 对名为killPhone的cookie的验证逻辑放在程序中处理，而不是直接报错
        if (phone == null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }

        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId,phone,md5);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (RepeatKillException e1) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (SeckillCloseException e2) {
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (SeckillException e) {
            logger.error(e.getMessage(),e);
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
    }

    /**
     *  获取系统当前的时间
     * @return
     */
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> getCurTime(){
        Date curTime = new Date();
        return new SeckillResult(true,curTime.getTime());
    }
}
