package com.luna.hbase.mapper;

import com.luna.hbase.annotation.HbaseColumn;
import com.luna.hbase.api.Mapper;
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

    private final Logger log = LoggerFactory.getLogger(RowMapper.class);

    @Override
    @SuppressWarnings("unchecked")
    public T mapping(Result result) throws Exception {
        Assert.notNull(result, "Result could not be null!");

        Class<?> clazz = ResolvableType.forClass(this.getClass()).getSuperType().getGeneric(0).resolve();
        Assert.notNull(clazz, "Class could not be null!");

        T entity = (T) clazz.newInstance();
        List<Cell> cells = result.listCells();
        for (Cell cell : cells) {
            String column = HBaseUtil.column(cell);
            String value = HBaseUtil.value(cell);
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                ReflectionUtils.makeAccessible(field);
                if (field.isAnnotationPresent(HbaseColumn.class)) {
                    HbaseColumn hbaseColumn = field.getAnnotation(HbaseColumn.class);
                    String columnName = hbaseColumn.value();
                    // 如果和列名相等，则获取set方法进行赋值
                    if (column.equals(columnName)) {
                        String setMethodName = "set" + HBaseUtil.upperCaseFirstLetter(field.getName());
                        // 注意set方法一定要带上参数，否则获取不到，因为不带参数默认寻找无参方法。
                        Method setMethod = ReflectionUtils.findMethod(clazz, setMethodName, field.getType());
                        try {
                            if (Objects.nonNull(setMethod)) {
                                // 注意实体类的字段类型必须是string，否则会报arguments type mismatch，因为hbase取出来的字段类型是string
                                ReflectionUtils.invokeMethod(setMethod, entity, value);
                            }
                        } catch (Exception e) {
                            log.error("通过反射调用set方法发生异常", e);
                        }
                    }
                }

            }
        }
        return entity;
    }
}