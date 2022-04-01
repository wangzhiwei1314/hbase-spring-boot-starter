package com.luna.hbase.definition;

import com.luna.hbase.entity.HbaseCell;
import org.apache.hadoop.hbase.client.Scan;

import java.util.List;
import java.util.Map;

/**
 * @author Austin Wong
 * @description Hbase操作API
 * @date 2022/3/30 15:55
 * @since JDK1.8
 */
public interface HbaseOperation {

    <T> T execute(HbaseCallback<T> callback);

    boolean createTable(String tableName, String... families);

    List<String> listTable();

    void put(String tableName, String rowKey, String family, String[] columns, String[] values);

    void delete(String tableName, String rowKey);

    String get(String tableName, String rowKey, String family, String column);

    <T> T get(String tableName, String rowKey, Mapper<T> mapper);

    <T> List<T> list(String tableName, Scan scan, Mapper<T> mapper);

    public List<HbaseCell> find(String tableName, String rowKey);

    public Map<String, List<HbaseCell>> scan(String tableName, Scan scan);
}
