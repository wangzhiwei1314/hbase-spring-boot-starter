package com.luna.hbase.connection.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.hbase.client.Connection;

/**
 *
 * Hbase connection pool.
 * @author David Wong
 * @version 1.0.0
 * @since JDK1.8
 * @see GenericObjectPool
 */
public class HbaseConnectionPool extends GenericObjectPool<Connection> {

    public HbaseConnectionPool(PooledObjectFactory<Connection> factory) {
        super(factory);
    }

    public HbaseConnectionPool(PooledObjectFactory<Connection> factory, GenericObjectPoolConfig<Connection> config) {
        super(factory, config);
    }

    public HbaseConnectionPool(PooledObjectFactory<Connection> factory, GenericObjectPoolConfig<Connection> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }

}
