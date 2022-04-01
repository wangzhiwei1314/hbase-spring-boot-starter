package com.luna.hbase.utils;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Random;

/**
 * @author Austin Wong
 * @description Hbase 工具类
 * @date 2022/3/30 14:28
 * @since JDK1.8
 */
public class HBaseUtil {

    //生成随机数字和字母,
    public static String randomString(int length) {

        StringBuilder val = new StringBuilder();
        Random random = new Random();

        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            } else {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }

    /**
     * 生成2位随机字符串 + 13位时间戳 + 业务编码的rowKey.
     *
     * @param bizCode
     * @return
     */
    public static String rawRowKey(String bizCode) {
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


    public static String upperCaseFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
