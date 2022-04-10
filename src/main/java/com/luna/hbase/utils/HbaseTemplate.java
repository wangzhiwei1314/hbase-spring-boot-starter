package com.luna.hbase.utils;

import com.luna.hbase.api.HbaseCallback;
import com.luna.hbase.api.HbaseOperations;
import com.luna.hbase.api.Mapper;
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
 * @description HBase Template
 * @date 2022/3/30 15:52
 * @since JDK1.8
 */
public class HbaseTemplate implements HbaseOperations {

    private final static Logger log = LoggerFactory.getLogger(HbaseTemplate.class);

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
                log.warn("table {} already exist!", name);
                return false;
            }

            List<ColumnFamilyDescriptor> list = Arrays.stream(columnFamilies)
                    .map(columnFamily -> ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamily)).build())
                    .collect(Collectors.toList());
            TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(name).setColumnFamilies(list).build();
            admin.createTable(tableDescriptor);
            log.info("create table {} successfully!", tableName);
            return false;

        });
    }

    @Override
    public List<String> listTableName() {
        return this.execute(connection -> {
            Admin admin = connection.getAdmin();
            TableName[] tableNames = admin.listTableNames();
            return Arrays.stream(tableNames).map(TableName::getNameAsString).collect(Collectors.toList());
        });
    }

    @Override
    @Deprecated
    public Map<String, List<HbaseCell>> scan(String tableName, Scan scan) {
        return this.execute(connection -> {
            Map<String, List<HbaseCell>> result;
            result = new HashMap<>(16);

            try (Table table = connection.getTable(TableName.valueOf(tableName))){
                ResultScanner scanner = table.getScanner(scan);
                for (Result i : scanner) {
                    String rowKey = null;
                    List<HbaseCell> row = new ArrayList<>();
                    for (Cell cell : i.listCells()) {
                        if (Objects.isNull(rowKey)) {
                            rowKey = HBaseUtil.rowKey(cell);
                        }
                        row.add(new HbaseCell(rowKey, HBaseUtil.family(cell), HBaseUtil.column(cell), HBaseUtil.value(cell)));
                    }
                    result.put(rowKey, row);
                }
            } catch (IOException e) {
                log.error("An exception occurred while scanning table", e);
            }

            return result;
        });
    }

    @Override
    public <T> List<T> list(String tableName, Scan scan, Mapper<T> mapper) {
        return this.execute(connection -> {
            List<T> list = new ArrayList<>();
            try (Table table = connection.getTable(TableName.valueOf(tableName))){
                ResultScanner resultScanners = table.getScanner(scan);
                for (Result result : resultScanners) {
                    list.add(mapper.mapping(result));
                }
            } catch (IOException e) {
                log.error("An exception occurred when list table", e);
            }
            return list;
        });
    }

    @Override
    public void put(String tableName, String rowKey, String columnFamily, String[] columnQualifiers, String[] values) {
        this.execute(connection -> {
            try (Table table = connection.getTable(TableName.valueOf(tableName))){
                Put put = new Put(Bytes.toBytes(rowKey));
                for (int i = 0, columnQualifiersLength = columnQualifiers.length; i < columnQualifiersLength; i++) {
                    String columnQualifier = columnQualifiers[i];
                    put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnQualifier), Bytes.toBytes(values[i]));
                }
                table.put(put);
            } catch (IOException e) {
                log.error("An exception occurred when put row into table", e);
            }
            return null;
        });
    }

    @Override
    public void delete(String tableName, String rowKey) {
        this.execute(connection -> {
            try (Table table = connection.getTable(TableName.valueOf(tableName))){
                Delete delete = new Delete(rowKey.getBytes());
                table.delete(delete);
            } catch (IOException e) {
                log.error("An exception occurred when delete row", e);
            }
            return null;
        });
    }

    @Override
    @Deprecated
    public List<HbaseCell> find(String tableName, String rowKey) {
        return this.execute(connection -> {
            List<HbaseCell> list = new ArrayList<>();
            try (Table table = connection.getTable(TableName.valueOf(tableName))){
                Get get = new Get(rowKey.getBytes());
                Result result = table.get(get);
                for (Cell cell : result.rawCells()) {
                    String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                    String column = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    list.add(new HbaseCell(rowKey, family, column, value));
                }
            } catch (IOException e) {
                log.error("An exception occurred when find row", e);
            }
            return list;
        });
    }

    @Override
    public <T> T get(String tableName, String rowKey, Mapper<T> mapper) {
        return this.execute(connection -> {
            try (Table table = connection.getTable(TableName.valueOf(tableName))){
                Get get = new Get(rowKey.getBytes());
                Result result = table.get(get);
                return mapper.mapping(result);
            } catch (IOException e) {
                log.error("An exception occurred when get row", e);
            }
            return null;
        });
    }

    @Override
    public String get(String tableName, String rowKey, String family, String column) {
        return this.execute(connection -> {
            try (Table table = connection.getTable(TableName.valueOf(tableName))){
                Get get = new Get(rowKey.getBytes());
                get.addColumn(family.getBytes(), column.getBytes());
                Result result = table.get(get);
                Cell cell = result.listCells().stream().findFirst().orElse(null);
                table.close();
                if (Objects.nonNull(cell)) {
                    return HBaseUtil.value(cell);
                }
            } catch (IOException e) {
                log.error("An exception occurred when get value", e);
            }
            return null;
        });
    }

    @Override
    public <T> T execute(HbaseCallback<T> callback) {
        Assert.notNull(callback, "Callback of Hbase must not be null");

        Connection connection = null;
        try {
            connection = pool.borrowObject();
            return callback.doInHbase(connection);
        } catch (Exception e) {
            throw new HbaseException("An exception occurred when operate HBase via connection", e);
        } finally {
            if (Objects.nonNull(connection)) {
                pool.returnObject(connection);
            }
        }
    }


}
