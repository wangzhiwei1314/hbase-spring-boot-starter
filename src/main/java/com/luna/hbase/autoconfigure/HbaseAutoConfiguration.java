package com.luna.hbase.autoconfigure;

import com.luna.hbase.connection.factory.HbaseConnectionFactory;
import com.luna.hbase.connection.pool.HbaseConnectionPool;
import com.luna.hbase.utils.HbaseTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Austin Wong
 * Hbase自动配置类
 * 2022/4/1 16:01
 * @since JDK1.8
 */
@Configuration
@EnableConfigurationProperties(HbaseProperties.class)
@ConditionalOnClass(HbaseTemplate.class)
@ConditionalOnProperty(name = "spring.hbase.enable", havingValue = "true")
public class HbaseAutoConfiguration {

    private final static Logger log = LoggerFactory.getLogger(HbaseConnectionFactory.class);

    @Bean
    @ConditionalOnMissingBean
    public org.apache.hadoop.conf.Configuration configuration(HbaseProperties hbaseProperties) {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();

        String rootDir = hbaseProperties.getRootDir();
        Assert.hasText(rootDir, "Root dir of HBase could not be null!");
        configuration.set("hbase.rootdir", rootDir);

        String quorum = hbaseProperties.getZookeeper().getQuorum();
        if (StringUtils.isNoneBlank(quorum)) {
            configuration.set("hbase.zookeeper.quorum", quorum);
        }

        String znodeParent = hbaseProperties.getZookeeper().getZnodeParent();
        if (StringUtils.isNotBlank(znodeParent)) {
            configuration.set("zookeeper.znode.parent", znodeParent);
        }

        return configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public HbaseConnectionPool hbaseConnectionPool(HbaseProperties hbaseProperties, org.apache.hadoop.conf.Configuration hbaseConfiguration) {
        HbaseProperties.HbasePoolConfiguration hbasePoolConfiguration = hbaseProperties.getPoolConfig();
        log.info("HBase connection pool initiating successfully!");
        log.info("{}", hbasePoolConfiguration.toString());

        return new HbaseConnectionPool(new HbaseConnectionFactory(hbaseConfiguration), hbasePoolConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean
    public HbaseTemplate hbaseTemplate(HbaseConnectionPool hbaseConnectionPool) {
        HbaseTemplate hbaseTemplate = new HbaseTemplate(hbaseConnectionPool);

        // Preheating zookeeper connection.
        new Thread(() -> {
            List<String> tableNameList = hbaseTemplate.listTableName();
            log.info("Current tables in Hbase are [{}]", String.join(",", tableNameList));
        }).start();

        return hbaseTemplate;
    }

}
