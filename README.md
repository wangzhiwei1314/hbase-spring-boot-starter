####spring-boot集成Hbase的starter


###使用说明
@Component
public class AreaRowMapper extends RowMapper<AreaEntity> {

}

####配置说明
hbase:
 enable: true
 root-dir: hdfs://ip:port/hbase
 zookeeper:
  quorum: ip:port
 pool-config:
  min-idle: 1
  max-idle: 10
  max-total: 100
