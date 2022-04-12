package com.luna.hbase.api;

import com.luna.hbase.entity.HbaseCell;
import org.apache.hadoop.hbase.client.Scan;

import java.util.List;
import java.util.Map;

/**
 * @author Austin Wong
 * Hbase操作API
 * 2022/3/30 15:55
 * @since JDK1.8
 */
public interface HbaseOperations {

    /**
     * 执行Hbase callback操作
     * @param callback Hbase回调接口实现
     * @param <T> 泛型
     * @return T
     */
    <T> T execute(HbaseCallback<T> callback);

    /**
     * 创建表
     * @param tableName 表名
     * @param families 列族
     * @return boolean
     */
    boolean createTable(String tableName, String... families);

    /**
     * 遍历所有表名
     * @return 表名列表
     */
    List<String> listTableName();

    /**
     * 保存一行数据
     * @param tableName 表名
     * @param rowKey 行键
     * @param family 列族
     * @param columns 列数组
     * @param values  值数组
     */
    void put(String tableName, String rowKey, String family, String[] columns, String[] values);

    /**
     * 删除某行数据
     * @param tableName 表名
     * @param rowKey 行键
     */
    void delete(String tableName, String rowKey);

    /**
     * 查询某列的值
     * @param tableName 表名
     * @param rowKey 行键
     * @param family 列族
     * @param column 列
     * @return String
     */
    String get(String tableName, String rowKey, String family, String column);

    /**
     * 查询某行数据，返回对象
     * @param <T> 泛型对象
     * @param tableName 表名
     * @param rowKey 行键
     * @param mapper row mapper
     * @return 泛型对象
     */
    <T> T get(String tableName, String rowKey, Mapper<T> mapper);

    /**
     * 查询多行数据，返回对象集合
     * @param <T> 泛型对象
     * @param tableName 表名
     * @param scan 扫描条件
     * @param mapper row mapper
     * @return 对象列表
     */
    <T> List<T> list(String tableName, Scan scan, Mapper<T> mapper);

    /**
     * 查询某行数据
     * @param tableName 表名
     * @param rowKey 行键
     * @return HbaseCell列表
     */
    List<HbaseCell> find(String tableName, String rowKey);

    /**
     * 查询多行数据
     * @param tableName 表名
     * @param scan 扫描条件
     * @return 数据cell集合
     */
    Map<String, List<HbaseCell>> scan(String tableName, Scan scan);
}
