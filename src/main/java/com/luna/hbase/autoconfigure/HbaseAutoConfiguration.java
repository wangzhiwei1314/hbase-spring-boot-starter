package com.luna.hbase.autoconfigure;

import com.luna.hbase.pool.factory.HbaseConnectionFactory;
import com.luna.hbase.utils.HbaseTemplate;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author Austin Wong
 * @description Hbase自动配置类
 * @date 2022/4/1 16:01
 * @since JDK1.8
 */
@Configuration
@EnableConfigurationProperties(HbaseProperties.class)
@ConditionalOnClass(HbaseTemplate.class)
@ConditionalOnProperty(name = "hbase.enable", havingValue = "true")
public class HbaseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GenericObjectPool<Connection> connectionPool(HbaseProperties hbaseProperties) {
        GenericObjectPoolConfig<Connection> poolConfig = hbaseProperties.getPoolConfig();
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", hbaseProperties.getZookeeper().getQuorum());
        configuration.set("hbase.rootdir", hbaseProperties.getRootDir());

        String znodeParent = hbaseProperties.getZookeeper().getZnodeParent();
        if (Objects.nonNull(znodeParent)) {
            configuration.set("zookeeper.znode.parent", znodeParent);
        }
        return new GenericObjectPool<>(new HbaseConnectionFactory(configuration), poolConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public HbaseTemplate hbaseTemplate(GenericObjectPool<Connection> pool) {
        return new HbaseTemplate(pool);
    }


}
