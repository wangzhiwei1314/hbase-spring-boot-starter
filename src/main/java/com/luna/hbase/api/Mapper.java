package com.luna.hbase.api;

import org.apache.hadoop.hbase.client.Result;

/**
 * @author Allen Wong
 * @version V1.0.0
 * Hbase结果映射接口
 * 2022/3/31 21:20
 */
public interface Mapper<T> {

    /**
     * 将result转换为T
     * @param result hbase查询出的结果
     * @return T
     * @throws Exception 异常
     */
    T mapping(Result result) throws Exception;

}
