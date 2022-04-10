# hbase-spring-boot-starter

## 简介

由于spring-boot组件缺乏对Hbase的支持，因此封装该starter，提高java语言使用hbase的效率。欢迎朋友们使用并加星，如果有问题请及时联系我，邮箱：austin_wong@sina.com

## 使用要求

spring-boot版本：不低于2.1.0.RELEASE

JDK版本：不低于 1.8

## 使用说明

### 1.引入maven依赖，本依赖已上传至公网，可直接引用

```xml
    <dependency>
        <groupId>io.github.wangzhiwei1314</groupId>
        <artifactId>hbase-spring-boot-starter</artifactId>
        <version>1.0.1.RELEASE</version>
    </dependency>

```

### 2.创建java映射对象，注意属性必须是String类型，属性保持和Hbase中的column一致，或在属性上增加@HbaseColumn注解，如

```java
public class Area {

  @HbaseColumn("AREA_NAME_")
  private String areaName;

}

```

### 3.编写Mapper类，继承RowMapper<T>类，增加@Repository注解，如

```java
@Repository
public class AreaRowMapper extends RowMapper<Area> {}

```

### 4.通过@Autowired注入hbaseTemplate和areaRowMapper，即可使用hbaseTemplate进行Hbase相关API操作。

```java
public class HBaseService {

  @Autowired
  private HbaseTemplate hbaseTemplate;

  @Autowired
  private AreaRowMapper areaRowMapper;

}

```

### 5.代码示例

```java
public class HBaseController {

  @Autowired
  private HbaseTemplate hbaseTemplate;

  @Autowired
  private AreaRowMapper areaRowMapper;

  public List<Area> list() {
    Scan scan = new Scan();
    scan.setFilter(new PageFilter(10));
    List<Area> list = this.hbaseTemplate.list("area", scan, areaRowMapper);
  }

}

```

## 配置文件说明

```yaml
    hbase:
	#true启用，false禁用
     enable: true
	 #Hbase根目录
     root-dir: hdfs://ip:port/hbase
     zookeeper:
	 #zookeeper地址
      quorum: ip:port
	 #连接池配置
     pool-config:
      min-idle: 1
      max-idle: 10
      max-total: 100
```

### 特性

* 开箱即用，配置简单

* 支持将Hbase行数据结果自动封装为java对象，易于操作

* 支持连接池管理，提升Hbase操作效率

* 丰富的API，可以满足大部分Hbase使用場景
