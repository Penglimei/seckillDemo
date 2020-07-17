package com.plm.exception;

/**
 *  秒杀相关业务异常,基础的异常类
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
