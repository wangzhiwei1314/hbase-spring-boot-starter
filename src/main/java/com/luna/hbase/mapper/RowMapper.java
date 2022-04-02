package com.luna.hbase.mapper;

import com.luna.hbase.annotation.HbaseColumn;
import com.luna.hbase.api.Mapper;
import com.luna.hbase.exception.HbaseException;
import com.luna.hbase.utils.HBaseUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author Allen Wong
 * @version V1.0.0
 * @description Hbase结果映射实现类
 * @date 2022/3/31 21:44
 */
public class RowMapper<T> implements Mapper<T> {

    private final static Logger log = LoggerFactory.getLogger(RowMapper.class);

    @Override
    @SuppressWarnings("unchecked")
    public T mapping(Result result) throws Exception {
        Assert.notNull(result, "Result could not be null!");

        Class<?> clazz = ResolvableType.forClass(this.getClass()).getSuperType().getGeneric(0).resolve();
        Assert.notNull(clazz, "Class could not be null!");

        T entity = (T) clazz.newInstance();
        List<Cell> cells = result.listCells();
        for (Cell cell : cells) {
            String columnQualifier = HBaseUtil.column(cell);
            String value = HBaseUtil.value(cell);
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                Class<?> type = field.getType();
                if (String.class != type) {
                    throw new HbaseException("The field present with @HbaseColumn must be String type!");
                }

                ReflectionUtils.makeAccessible(field);
                if (field.getName().equalsIgnoreCase(columnQualifier)) {
                    ReflectionUtils.setField(field, entity, value);
                } else if (field.isAnnotationPresent(HbaseColumn.class)) {
                    String hbaseColumn = field.getAnnotation(HbaseColumn.class).value();
                    if (columnQualifier.equals(hbaseColumn)) {
                        ReflectionUtils.setField(field, entity, value);
                    }
                }

            }
        }
        return entity;
    }
}
