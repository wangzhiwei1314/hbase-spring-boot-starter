package com.luna.hbase.autoconfigure;

import com.luna.hbase.connection.factory.HbaseConnectionFactory;
import com.luna.hbase.utils.HbaseTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * @author Austin Wong
 * @description Hbase自动配置类
 * @date 2022/4/1 16:01
 * @since JDK1.8
 */
@Configuration
@EnableConfigurationProperties(HbaseProperties.class)
@ConditionalOnClass(HbaseTemplate.class)
@ConditionalOnProperty(name = "spring.hbase.enable", havingValue = "true")
public class HbaseAutoConfiguration {

    private final static Logger log = LoggerFactory.getLogger(HbaseConnectionFactory.class);

    private static final String HBASE_ROOT_DIR = "hbase.rootdir";

    private static final String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";

    private static final String ZOOKEEPER_ZNODE_PARENT = "zookeeper.znode.parent";

    @Bean
    @ConditionalOnMissingBean
    public GenericObjectPool<Connection> connectionPool(HbaseProperties hbaseProperties) {
        GenericObjectPoolConfig<Connection> poolConfig = hbaseProperties.getPoolConfig();
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        String rootDir = hbaseProperties.getRootDir();
        Assert.hasText(rootDir, "Root dir of HBase could not be null!");
        configuration.set(HBASE_ROOT_DIR, rootDir);

        String quorum = hbaseProperties.getZookeeper().getQuorum();
        if (StringUtils.isNoneBlank(quorum)) {
            configuration.set(HBASE_ZOOKEEPER_QUORUM, quorum);
        }
        String znodeParent = hbaseProperties.getZookeeper().getZnodeParent();
        if (StringUtils.isNotBlank(znodeParent)) {
            configuration.set(ZOOKEEPER_ZNODE_PARENT, znodeParent);
        }

        log.info("HBase connection pool initiating successfully! min-idle is {}, max-idle is {}, max-total is {}",
                poolConfig.getMinIdle(), poolConfig.getMaxIdle(), poolConfig.getMaxTotal());
        return new GenericObjectPool<>(new HbaseConnectionFactory(configuration), poolConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public HbaseTemplate hbaseTemplate(GenericObjectPool<Connection> pool) {
        return new HbaseTemplate(pool);
    }


}
