package com.luna.hbase.utils;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Random;

/**
 * @author Austin Wong
 * Hbase 工具类
 * 2022/3/30 14:28
 * @since JDK1.8
 */
public class HBaseUtil {

    /**
     * 生成随机字母或数字
     *
     * @param length 长度
     * @return String
     */
    public static String randomString(int length) {
        StringBuilder string = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if ("char".equalsIgnoreCase(charOrNum)) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                string.append((char) (random.nextInt(26) + temp));
            } else {
                string.append(random.nextInt(10));
            }
        }
        return string.toString();
    }

    /**
     * 生成2位随机字符串 + 13位时间戳 + 业务编码的rowKey.
     *
     * @param bizCode 业务代码
     * @return String
     */
    public static String generateRowKey(String bizCode) {
        return randomString(2) + System.currentTimeMillis() + bizCode;
    }

    public static String rowKey(Cell cell) {
        return Bytes.toString(CellUtil.cloneRow(cell));
    }

    public static String family(Cell cell) {
        return Bytes.toString(CellUtil.cloneFamily(cell));
    }

    public static String column(Cell cell) {
        return Bytes.toString(CellUtil.cloneQualifier(cell));
    }

    public static String value(Cell cell) {
        return Bytes.toString(CellUtil.cloneValue(cell));
    }

    /**
     * 将字符串首字母大写
     * @param word 输入的字符串
     * @return String
     */
    public static String upperCaseFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
