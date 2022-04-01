package com.luna.hbase.definition;

import org.apache.hadoop.hbase.client.Connection;

/**
 * @author Austin Wong
 * @description todo
 * @date 2022/3/31 17:53
 * @since JDK1.8
 */
@FunctionalInterface
public interface HbaseCallback<T> {

    T doInHbase(Connection connection) throws Exception;

}
