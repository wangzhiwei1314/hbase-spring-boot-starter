package com.luna.hbase.api;

import org.apache.hadoop.hbase.client.Result;

/**
 * @author Allen Wong
 * @version V1.0.0
 * @description Hbase结果映射接口
 * @date 2022/3/31 21:20
 */
public interface Mapper<T> {

    /**
     * 将result转换为T
     * @param result
     * @return
     * @throws Exception
     */
    T mapping(Result result) throws Exception;

}
