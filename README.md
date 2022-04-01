##hbase-spring-boot-starter说明

由于spring-boot组件缺乏对Hbase的支持，因此封装该starter，提高java语言使用hbase的效率

###使用要求

spring-boot版本：不低于2.1.0.RELEASE

JDK版本：不低于1.8

###使用说明

1.引入依赖

    <dependency>
        <groupId>com.luna</groupId>
        <artifactId>hbase-spring-boot-starter</artifactId>
        <version>1.0.0.RELEASE</version>
    </dependency>
    
2.编写java对象，注意属性必须是String类型，属性上增加@HbaseColumn注解，如

    @HbaseColumn("AREA_NAME_")
    private String areaName;

3.编写Mapper类，继承RowMapper<T>类，增加@Repository注解，如

    @Repository
    public class AreaRowMapper extends RowMapper<Area> {

    }
    
4.通过@Autowired注入hbaseTemplate和areaRowMapper，即可使用hbaseTemplate进行Hbase相关API操作。

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private AreaRowMapper areaRowMapper;
        
5.代码示例

     Scan scan = new Scan();
     scan.setFilter(new PageFilter(10));
     List<Area> list = this.hbaseTemplate.list("area", scan, areaRowMapper);
     
###配置文件说明

    hbase:
     enable: true
     root-dir: hdfs://ip:port/hbase
     zookeeper:
      quorum: ip:port
     pool-config:
      min-idle: 1
      max-idle: 10
      max-total: 100
      
###特性

1.开箱即用，配置简单

2.支持将行数据封装为java对象，易于操作

3.支持连接池管理，提升对Hbase操作效率

4.丰富的API，可以满足大部分对于Hbase的操作需求