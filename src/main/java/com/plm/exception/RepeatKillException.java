package com.plm.exception;


/**
 *  重复秒杀异常（运行期异常）
 *      避免用户使用 软件进行恶意秒杀或者是无意点击多次秒杀
 *
 *  Mysql只支持运行期异常的回滚操作
 */
public class RepeatKillException extends SeckillException{

    public RepeatKillException (String message){
        super(message);
    }

    public RepeatKillException (String message,Throwable cause){
        super(message,cause);
    }
}
