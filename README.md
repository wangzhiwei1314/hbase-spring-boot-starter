# hbase-spring-boot-starter

## 简介

由于 spring-boot 组件缺乏对 Hbase 的支持，因此封装该 starter，提高 java 语言使用 hbase 的效率。欢迎朋友们使用并加星，如果有问题请及时联系我，邮箱：austin_wong@sina.com

## 使用要求

spring-boot 版本：不低于 2.1.0.RELEASE

JDK 版本：不低于 1.8

## 使用说明

### 1.引入maven依赖，本依赖已上传至公网，可直接引用

```xml
    <dependency>
        <groupId>io.github.wangzhiwei1314</groupId>
        <artifactId>hbase-spring-boot-starter</artifactId>
        <version>1.0.0.RELEASE</version>
    </dependency>

```

### 2.创建 java 映射对象，注意属性必须是 String 类型，属性保持和 Hbase 中的 column 一致，或在属性上增加@HbaseColumn 注解，如

```java
public class Area {

  @HbaseColumn("AREA_NAME_")
  private String areaName;
}

```

### 3.编写 Mapper 类，继承 RowMapper<T>类，增加@Repository 注解，如

```java
@Repository
public class AreaRowMapper extends RowMapper<Area> {}

```

### 4.通过@Autowired 注入 hbaseTemplate 和 areaRowMapper，即可使用 hbaseTemplate 进行 Hbase 相关 API 操作。

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

* 支持将 Hbase 行数据结果自动封装为 java 对象，易于操作

* 支持连接池管理，提升 Hbase 操作效率

* 丰富的 API，可以满足大部分对于 Hbase 的操作需求
