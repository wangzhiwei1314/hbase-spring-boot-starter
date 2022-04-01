package com.luna.hbase.autoconfigure;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.hbase.client.Connection;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.luna.hbase.autoconfigure.HbaseProperties.HBASE_PREFIX;

/**
 * @author Austin Wong
 * @description Hbase.properties
 * @date 2022/4/1 15:54
 * @since JDK1.8
 */
@ConfigurationProperties(prefix = HBASE_PREFIX)
public class HbaseProperties {

    public static final String HBASE_PREFIX = "spring.hbase";

    private Boolean enable;

    private Zookeeper zookeeper;

    private String rootDir;

    GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();

    public GenericObjectPoolConfig<Connection> getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(GenericObjectPoolConfig<Connection> poolConfig) {
        this.poolConfig = poolConfig;
    }

    public Zookeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(Zookeeper zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public static class Zookeeper {

        private String quorum;

        private String znodeParent;

        public String getQuorum() {
            return quorum;
        }

        public void setQuorum(String quorum) {
            this.quorum = quorum;
        }

        public String getZnodeParent() {
            return znodeParent;
        }

        public void setZnodeParent(String znodeParent) {
            this.znodeParent = znodeParent;
        }
    }


}
