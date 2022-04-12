package com.luna.hbase.autoconfigure;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.hbase.client.Connection;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.luna.hbase.autoconfigure.HbaseProperties.HBASE_PREFIX;

/**
 * @author Austin Wong
 * Hbase properties.
 * 2022/4/1 15:54
 * @since JDK1.8
 */
@ConfigurationProperties(prefix = HBASE_PREFIX)
public class HbaseProperties {

    public static final String HBASE_PREFIX = "spring.hbase";

    private Boolean enable;

    private Zookeeper zookeeper;

    private String rootDir;

    HbasePoolConfiguration poolConfig = new HbasePoolConfiguration();

    public HbasePoolConfiguration getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(HbasePoolConfiguration poolConfig) {
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

    public static class HbasePoolConfiguration extends GenericObjectPoolConfig<Connection> {

        private int maxTotal = DEFAULT_MAX_TOTAL;

        private int maxIdle = DEFAULT_MAX_IDLE;

        private int minIdle = DEFAULT_MIN_IDLE;

        private boolean lifo = DEFAULT_LIFO;

        private boolean fairness = DEFAULT_FAIRNESS;

        private long maxWaitMillis = DEFAULT_MAX_WAIT_MILLIS;

        private boolean testOnCreate = DEFAULT_TEST_ON_CREATE;

        private boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;

        private boolean testOnReturn = DEFAULT_TEST_ON_RETURN;

        private boolean testWhileIdle = DEFAULT_TEST_WHILE_IDLE;

        private long timeBetweenEvictionRunsMillis =
                DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

        private boolean blockWhenExhausted = DEFAULT_BLOCK_WHEN_EXHAUSTED;

        private String jmxNamePrefix = DEFAULT_JMX_NAME_PREFIX;

        private String jmxNameBase = DEFAULT_JMX_NAME_BASE;

        @Override
        public int getMaxTotal() {
            return maxTotal;
        }

        @Override
        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }

        @Override
        public int getMaxIdle() {
            return maxIdle;
        }

        @Override
        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        @Override
        public int getMinIdle() {
            return minIdle;
        }

        @Override
        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public boolean isLifo() {
            return lifo;
        }

        @Override
        public void setLifo(boolean lifo) {
            this.lifo = lifo;
        }

        public boolean isFairness() {
            return fairness;
        }

        @Override
        public void setFairness(boolean fairness) {
            this.fairness = fairness;
        }

        @Override
        public long getMaxWaitMillis() {
            return maxWaitMillis;
        }

        @Override
        public void setMaxWaitMillis(long maxWaitMillis) {
            this.maxWaitMillis = maxWaitMillis;
        }

        public boolean isTestOnCreate() {
            return testOnCreate;
        }

        @Override
        public void setTestOnCreate(boolean testOnCreate) {
            this.testOnCreate = testOnCreate;
        }

        public boolean isTestOnBorrow() {
            return testOnBorrow;
        }

        @Override
        public void setTestOnBorrow(boolean testOnBorrow) {
            this.testOnBorrow = testOnBorrow;
        }

        public boolean isTestOnReturn() {
            return testOnReturn;
        }

        @Override
        public void setTestOnReturn(boolean testOnReturn) {
            this.testOnReturn = testOnReturn;
        }

        public boolean isTestWhileIdle() {
            return testWhileIdle;
        }

        @Override
        public void setTestWhileIdle(boolean testWhileIdle) {
            this.testWhileIdle = testWhileIdle;
        }

        @Override
        public long getTimeBetweenEvictionRunsMillis() {
            return timeBetweenEvictionRunsMillis;
        }

        @Override
        public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
            this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        }

        public boolean isBlockWhenExhausted() {
            return blockWhenExhausted;
        }

        @Override
        public void setBlockWhenExhausted(boolean blockWhenExhausted) {
            this.blockWhenExhausted = blockWhenExhausted;
        }

        @Override
        public boolean getJmxEnabled() {
            /**
             *  Jmx must be false because there often exist an instance of GenericObjectPool.
             */
            return false;
        }

        @Override
        public String getJmxNamePrefix() {
            return jmxNamePrefix;
        }

        @Override
        public void setJmxNamePrefix(String jmxNamePrefix) {
            this.jmxNamePrefix = jmxNamePrefix;
        }

        @Override
        public String getJmxNameBase() {
            return jmxNameBase;
        }

        @Override
        public void setJmxNameBase(String jmxNameBase) {
            this.jmxNameBase = jmxNameBase;
        }


    }
}
