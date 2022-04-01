package com.luna.hbase.entity;

import java.io.Serializable;

/**
 * @author Allen Wong
 * @version V1.0.0
 * @description HBase cell实体类
 * @date 2022/3/26 21:59
 */
public class HbaseCell implements Serializable {
    private static final long serialVersionUID = -1458436889946478194L;

    private String rowKey;

    private String family;

    private String column;

    private String value;

    public HbaseCell(String rowKey, String family, String column, String value) {
        this.rowKey = rowKey;
        this.family = family;
        this.column = column;
        this.value = value;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
