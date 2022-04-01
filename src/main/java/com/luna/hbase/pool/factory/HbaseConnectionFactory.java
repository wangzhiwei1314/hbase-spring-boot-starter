package com.luna.hbase.pool.factory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author Allen Wong
 * @version V1.0.0
 * @description todo
 * @date 2022/3/26 20:48
 */
public class HbaseConnectionFactory extends BasePooledObjectFactory<Connection> {

    private final Logger log = LoggerFactory.getLogger(HbaseConnectionFactory.class);

    private final Configuration configuration;

    public HbaseConnectionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Connection create() throws Exception {
        return ConnectionFactory.createConnection(configuration);
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return new DefaultPooledObject<>(connection);
    }

    @Override
    public void destroyObject(final PooledObject<Connection> pooledObject) throws Exception {
        Objects.requireNonNull(pooledObject.getObject()).close();
    }

    @Override
    public boolean validateObject(final PooledObject<Connection> pooledObject) {
        Connection connection = pooledObject.getObject();
        return !connection.isClosed();
    }

}