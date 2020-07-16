# seckillDemo
基于SpringMVC+Spring+MyBatis实现高并发秒杀API
# 项目介绍
## 秒杀？
> 所谓“秒杀”，就是网络卖家发布一些超低价格的商品，所有买家在同一时间网上抢购的一种销售方式。由于商品价格低廉，往往一上架就被抢购一空，有时只用一秒钟。
## 为何选用秒杀作为典型实战项目
> + 秒杀业务场景具有典型事务特性
> + 秒杀/红包类需求越来越常见
## 后端框架
> SpringMVC+Spring+MyBatis
> 原因：
> + 框架易于使用和轻量级
> + 低代码侵入性
> + 成熟的社区和用户群
## 收获
> + 框架使用和整合技巧
> + 秒杀分析过程与优化思路
## 项目来源
> [Java高并发秒杀API之业务分析与Dao层](https://www.imooc.com/view/587)  
> [Java高并发秒杀API之Web层](https://www.imooc.com/view/630)  
> [Java高并发秒杀API之Service层](https://www.imooc.com/view/631)  
> [Java高并发秒杀API之高并发优化](https://www.imooc.com/view/632)  
## 相关技术
> + MySQl  
> > 表设计  
> > SQL技巧  
> > 事务和行级锁  

> + MyBatis  
> > Dao层设计与开发  
> > MyBatis合理使用
> > 与Spring整合

> + Spring  
> > Spring IOC整合Service
> > 声明式事务运用

> + SpringMVC  
> > Restful接口设计与使用
> > 框架运作流程
> > Controller开发技巧

> + 前端  
> > 交互设计
> > BootStrap
> > jQuery

> + 高并发
> > 高并发点和高并发分析
> > 优化思路并实现
> > 优化思路并实现
## 开发环境
> + 操作系统
> > macOs Catalina

> + IDE工具
> > IDEA

> + JDK
> > JDK1.8
> > ```
> > /usr/libexec/java_home -V
> > ```

> + 数据库
> > 5.7.24 
> > ```
> > select version();
> > ```

> + 构建工具
> > Maven 3.6.0 
> > ```
> > mvn -v
> > ```

> + 框架
> > SpringMVC+Spring+MyBatis

# Java高并发秒杀API之业务分析与Dao层
## 创建项目
> 创建Maven项目
> > ![IDEA中创建Maven项目](/Users/penglimei/IntelliJ_IDEAProjects/Interview/seckillDemo/src/main/webapp/WEB-INF/pictures/IDEA中创建Maven项目.png)

> > Maven项目创建过程中要注意
> > > web.xml文件 servlet2.3 版本中不支持 jsp el ,需要将 servlet修改为更高的版本,本项目使用 servlet3.1版本  

> pox.xml中导入依赖
```xml
<dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>

    <!-- 补全项目依赖 -->
    <!--
          1、日志
         Java日志选择
            slf4j 是规范/接口
            日志实现：log4j,logback,common-logging
            本项目使用：slf4j+logback
     -->
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>1.7.12</version>
    </dependency>
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-core</artifactId>
      <version>1.1.1</version>
    </dependency>
    <!-- 实现slf4j接口的整合 -->
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.1.1</version>
    </dependency>

    <!-- 2、数据库相关依赖 -->
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.35</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>c3p0</groupId>
      <artifactId>c3p0</artifactId>
      <version>0.9.1.2</version>
    </dependency>

    <!-- 3、Dao框架：MyBatis依赖 -->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.3.0</version>
    </dependency>
    <!-- MyBatis自身实现的spring整合依赖 -->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis-spring</artifactId>
      <version>1.2.3</version>
    </dependency>

    <!-- 4、Servlet web相关依赖 -->
    <dependency>
      <groupId>taglibs</groupId>
      <artifactId>standard</artifactId>
      <version>1.1.2</version>
    </dependency>
    <dependency>
      <groupId>jstl</groupId>
      <artifactId>jstl</artifactId>
      <version>1.2</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.5.4</version>
    </dependency>
    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>javax.servlet-api</artifactId>
      <version>3.1.0</version>
    </dependency>

    <!-- 5、Spring依赖 -->
    <!-- 1)Spring核心依赖 -->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-core</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-beans</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>
    <!-- 2)Spring Dao层依赖 -->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-jdbc</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-tx</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>
    <!-- 3)Spring Web层相关依赖 -->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-web</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-webmvc</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>
    <!-- 4)Spring test相关依赖 -->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>
  </dependencies>
```

> > pom.xml中相关知识点
> > > project 标签报红
> > > + 进入maven的仓库 .m2/repository/文件夹，找到未加载的jar包文件夹删除，然后在工程里右击pom.xml文件-》maven-》reimport。

> > > Java日志选择
> > > > + slf4j 是规范/接口
> > > > + 日志实现：log4j,logback,common-logging
> > > > + 本项目使用：slf4j+logback

## 秒杀业务分析
> `秒杀的核心是：对库存的处理`   
> 流程 
> > + 商家"添加/调整"库存中的产品，商家根据库存"发货/核账"  
> > + 用户"秒杀/预售"库存中的产品，用户"付款/退货"库存会相应变动

> 用户针对库存业务分析
> > 1. 行为：
> > + 减库存
> > + 记录购买明细  
> > > 这两个操作属于一个完整事务，通过事务来实现数据落地(数据持久化)  
> > 2. 记录用户秒杀成功信息:
> > + 谁购买成功了
> > + 成功的时间/有效时间
> > + 付款/发货信息
> > 3. 为什么需要事务
> > + 减库存没有记录购买明细-->出现少卖现象
> > + 记录购买明细没有减库存-->出现超卖现象
> > 3. 数据落地
> > >  MYSQL: 事务机制是目前最可靠的落地方案。数据落地就是就是被持久化的数据，这种数据一般放在硬盘或是其他的持久化存储设备里。

> 使用mysql实现秒杀的难点分析  
> > `难点问题 --> 如何高效处理竞争`
> > > 竞争：多个用户同一时间抢购同一件物品  
> > > `竞争反映在mysql中就是 事务+行级锁`  
> > > 事务工作机制：  
> > > start transaction -> update 库存数量 -> insert 购买明细 -> commit  
> > > 其中 `update 库存数量 是竞争出现的部位，要用行级锁处理`

> 实现那些秒杀功能
> > + 秒杀接口暴露
> > + 执行秒杀
> > + 相关查询

## Dao层设计与开发
> 数据库创建
```mysql
# 数据库初始化脚本

# 创建数据库
create database seckill;
# 使用户数据库
use seckill;
CREATE TABLE seckill
(
    seckill_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存商品ID',
    name VARCHAR(120) NOT NULL COMMENT '商品名称',
    number INT NOT NULL COMMENT '库存数量',
    create_time TIMESTAMP NOT NULL default current_timestamp COMMENT '创始时间',
    start_time TIMESTAMP NOT NULL default current_timestamp COMMENT '秒杀开始时间',
    end_time TIMESTAMP NOT NULL default current_timestamp COMMENT '秒杀结束时间',
    PRIMARY KEY (seckill_id),
    KEY idx_start_time(start_time),
    KEY idx_end_time(end_time),
    KEY idx_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';


# 初始化数据
INSERT INTO seckill(name, number, start_time, end_time) VALUES
('1000元秒杀iphone6',100,'2021-07-16 00:00:00','2021-07-17 00:00:00'),
('800元秒杀ipad',200,'2021-07-16 00:00:00','2021-07-17 00:00:00'),
('6600元秒杀mac book pro',300,'2021-07-16 00:00:00','2021-07-17 00:00:00'),
('7000元秒杀iMac',400,'2021-07-16 00:00:00','2021-07-17 00:00:00');

# 秒杀成功明细表
# 用户登录认证相关信息(简化为手机号)
CREATE TABLE success_killed
(
    seckill_id BIGINT NOT NULL COMMENT '秒杀商品ID',
    user_phone BIGINT NOT NULL COMMENT '用户手机号',
    state TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
    create_time TIMESTAMP NOT NULL COMMENT '创建时间',
    PRIMARY KEY (seckill_id,user_phone),/*联合主键*/
    KEY idx_create_time(create_time)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';
```
### 数据库设计编码

### Dao实体类和接口编程
### MyBatis整合Spring

