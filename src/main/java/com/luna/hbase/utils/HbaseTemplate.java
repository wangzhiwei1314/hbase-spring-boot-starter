package com.luna.hbase.utils;

import com.luna.hbase.definition.HbaseCallback;
import com.luna.hbase.definition.HbaseOperation;
import com.luna.hbase.definition.Mapper;
import com.luna.hbase.entity.HbaseCell;
import com.luna.hbase.exception.HbaseException;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Austin Wong
 * @description HBaseTemplate
 * @date 2022/3/30 15:52
 * @since JDK1.8
 */
public class HbaseTemplate implements HbaseOperation {

    private final Logger log = LoggerFactory.getLogger(HbaseTemplate.class);

    protected GenericObjectPool<Connection> pool;

    public HbaseTemplate(GenericObjectPool<Connection> pool) {
        this.pool = pool;
    }

    @Override
    public boolean createTable(String tableName, String... columnFamilies) {
        return this.execute(connection -> {
            Admin admin = connection.getAdmin();
            TableName name = TableName.valueOf(tableName);
            if (admin.tableExists(name)) {
                log.warn("table exist!");
                return false;
            }

            List<ColumnFamilyDescriptor> list = Arrays.stream(columnFamilies)
                    .map(columnFamily -> ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamily)).build())
                    .collect(Collectors.toList());
            TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(name).setColumnFamilies(list).build();
            admin.createTable(tableDescriptor);
            log.warn("create table successfully!");
            return false;

        });
    }

    @Override
    public List<String> listTable() {
        return this.execute(connection -> {
            Admin admin = connection.getAdmin();
            TableName[] tableNames = admin.listTableNames();
            return Arrays.stream(tableNames).map(TableName::getNameAsString).collect(Collectors.toList());
        });
    }

    /**
     * 查询表中所有数据
     *
     * @param tableName
     * @return
     */
    @Override
    @Deprecated
    public Map<String, List<HbaseCell>> scan(String tableName, Scan scan) {
        return this.execute(connection -> {
            Map<String, List<HbaseCell>> result;
            Table table;
            result = new HashMap<>(16);
            table = connection.getTable(TableName.valueOf(tableName));
            ResultScanner scanner = table.getScanner(scan);

            for (Result i : scanner) {
                String rowKey = null;
                List<HbaseCell> row = new ArrayList<>();
                for (Cell cell : i.listCells()) {
                    // rowKey
                    if (Objects.isNull(rowKey)) {
                        rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                    }
                    HbaseCell hBaseCell = new HbaseCell(rowKey, HBaseUtil.family(cell), HBaseUtil.column(cell), HBaseUtil.value(cell));
                    row.add(hBaseCell);
                }
                result.put(rowKey, row);
            }
            table.close();
            return result;
        });
    }

    @Override
    public <T> List<T> list(String tableName, Scan scan, Mapper<T> mapper) {
        return this.execute(connection -> {
            List<T> result = new ArrayList<>();
            Table table;
            table = connection.getTable(TableName.valueOf(tableName));
            ResultScanner scanner = table.getScanner(scan);

            for (Result i : scanner) {
                result.add(mapper.mapping(i));
            }
            table.close();
            return result;
        });
    }

    /**
     * 保存数据
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param columnQualifiers
     * @param values
     * @throws IOException
     */
    @Override
    public void put(String tableName, String rowKey, String columnFamily, String[] columnQualifiers, String[] values) {
        this.execute(connection -> {
            Table table = connection.getTable(TableName.valueOf(tableName));

            Put put = new Put(Bytes.toBytes(rowKey));
            for (int i = 0, columnQualifiersLength = columnQualifiers.length; i < columnQualifiersLength; i++) {
                String columnQualifier = columnQualifiers[i];
                put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnQualifier), Bytes.toBytes(values[i]));
            }

            table.put(put);
            table.close();
            return null;
        });
    }

    /**
     * 删除行
     *
     * @param tableName
     * @param rowKey
     * @throws IOException
     */
    @Override
    public void delete(String tableName, String rowKey) {
        this.execute(connection -> {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(rowKey.getBytes());
            table.delete(delete);
            return null;
        });
    }

    @Override
    @Deprecated
    public List<HbaseCell> find(String tableName, String rowKey) {
        return this.execute(connection -> {
            List<HbaseCell> list = new ArrayList<>();
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            Result result = table.get(get);

            for (Cell cell : result.rawCells()) {
                String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                String column = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                log.info("family:{},column:{},value:{}", family, column, value);
                HbaseCell baseCell = new HbaseCell(rowKey, family, column, value);
                list.add(baseCell);
            }
            return list;
        });
    }

    @Override
    public <T> T get(String tableName, String rowKey, Mapper<T> mapper) {
        return this.execute(connection -> {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            Result result = table.get(get);
            return mapper.mapping(result);
        });
    }

    @Override
    public String get(String tableName, String rowKey, String family, String column) {
        return this.execute(connection -> {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            get.addColumn(family.getBytes(), column.getBytes());
            Result result = table.get(get);
            Cell cell = result.listCells().stream().findFirst().orElse(null);
            table.close();
            if (Objects.nonNull(cell)) {
                return HBaseUtil.value(cell);
            }
            return null;
        });
    }

    @Override
    public <T> T execute(HbaseCallback<T> callback) {
        Assert.notNull(callback, "Callback object must not be null");

        Connection connection = null;
        try {
            connection = pool.borrowObject();
            return callback.doInHbase(connection);
        } catch (Exception e) {
            throw new HbaseException("操作HBase发生异常", e);
        } finally {
            if (Objects.nonNull(connection)) {
                pool.returnObject(connection);
            }
        }
    }


}
