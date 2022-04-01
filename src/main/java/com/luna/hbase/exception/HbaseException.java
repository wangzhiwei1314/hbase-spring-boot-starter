package com.luna.hbase.exception;

/**
 * @author Austin Wong
 * @description Hbase异常
 * @date 2022/4/1 16:20
 * @since JDK1.8
 */
public class HbaseException extends RuntimeException{
    private static final long serialVersionUID = 6782792380828974528L;

    public HbaseException(String message) {
        super(message);
    }

    public HbaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
