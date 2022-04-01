package com.luna.hbase.api;

import org.apache.hadoop.hbase.client.Connection;

/**
 * @author Austin Wong
 * @description Hbase callback接口
 * @date 2022/3/31 17:53
 * @since JDK1.8
 */
@FunctionalInterface
public interface HbaseCallback<T> {
    /**
     * Do something in Hbase via connection.
     * @param connection
     * @return
     * @throws Exception
     */
    T doInHbase(Connection connection) throws Exception;

}
