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
> > 4. 数据落地
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
### 数据库设计编码
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
> > 之所以使用联合主键，是为了能够过滤重复插入，可以通过insert ignore into语句来避免用户重复秒杀同一件商品。这样当有重复记录就会忽略，语句执行后返回数字0。

### Dao实体类
> entity/Seckill.java // 实体类--》库存
```java
package com.plm.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *  库存 实体类
 */
public class Seckill implements Serializable {

    private long seckillId;
    private String name;
    private int number;
    private Date startTime;
    private Date endTime;
    private Date createTime;

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Seckill{" +
                "seckillId=" + seckillId +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                '}';
    }
}
```  

> entity/SuccessKilled.java // 实体类--》秒杀成功明细
```java
package com.plm.entity;


import java.io.Serializable;
import java.util.Date;

/**
 *  秒杀成功明细实体
 */
public class SuccessKilled implements Serializable {

    private long seckillId;
    private long userPhone;
    private short state;
    private Date createTime;
    // 多个 秒杀成功明细实体 对应 一个库存实体，多对一
    private Seckill seckill;

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}

```

> > 多对一：`多个秒杀成功明细实体 对应 一个 库存实体`  
### Dao实体类接口编程
> ORM(对象映射关系)
> + 就是作用在关系型数据库表和简单的java对象的映射关系模型，通过这层映射可以通过POJO对象操作数据库表
> + Java中典型的ORM有:  
>  hibernate、mybatis、jpa  

> MyBatis
> + 是一个持久化框架，支持自定义SQL查询、存储过程
> + 通过XML文件或者注解来配置映射关系，将接口与POJO对象映射到数据库记录中

> Mybatis 与 Hibernate的区别
> > + mybatis半自动，需要手写sql来实现和管理对象数据及关系，是一个不完全 ORM框架    
> >  hibernate全自动，完全可以通过 ORM实现对数据库的操作，是一个完全 ORM框架  
> > + mybatis 手写sql，可以进行优化  
> > hibernate的sql很多都是自动生成的无法直接维护

> dao/SeckillDao.java // 接口
```java
package com.plm.dao;

import com.plm.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SeckillDao {

    /**
     *  减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数 > 1，表示更新的记录行数
     */
    int reduceNumber(@Param("seckillId")long seckillId, @Param("killTime")Date killTime);

    /**
     *  根据 seckillId 查询秒杀的产品对象
     * @param seckillId
     * @return
     */
    Seckill findById(long seckillId);


    /**
     *  根据偏移量offset 查询秒杀产品列表
     * @param offset 偏移量
     * @param limit 从偏移量往后数要获取多少个产品的信息
     * @return
     *
     *  由于offset、limit两个参数传入方法时是形参arg0,arg1，
     *  SeckillDao.xml中获取两个参数时并不知道哪一个是谁，
     *  因此需要使用 MyBatis的注解@Param() 让SeckillDao.xml中获取两个参数时
     *  知道哪一个是offset，哪一个是limit
     *  @Param("offset")int offset, @Param("limit")int limit
     */
    List<Seckill> findAll(@Param("offset")int offset, @Param("limit")int limit);
}
```
> > `@Param()注解的作用，让SeckillDao.xml中获取两个参数时知道哪一个是offset，哪一个是limit`  

> dao/SuccessKilledDao.java // 接口
```java
package com.plm.dao;

import com.plm.entity.SuccessKilled;

public interface SuccessKilledDao {

    /**
     * 添加秒杀成功明细，可以通过联合主键过滤同一用户重复秒杀同一商品
     * @param seckillId
     * @param userPhone
     * @return 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);

    /**
     *  根据 seckillId查询秒杀成功的明细，以及所秒杀商品的信息
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled findByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);
}
```
> > `@Param()注解的作用`

> mybatis全局属性配置文件 mybatis-config.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 配置全局信息 -->
    <settings>
        <!-- 使用 jdbc的 useGeneratedKeys 获取数据库自增主键值 -->
        <setting name="useGeneratedKeys" value="true"/>
        <!-- 使用列别名替换列名 默认为true
            select name as title from table
         -->
        <setting name="useColumnLabel" value="true"/>
        <!-- 开启驼峰命名转换
                table(create_time)==>Entity(createTime)
         -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>
</configuration>
```

> mapper/SeckillDao.xml   // 映射配置文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.plm.dao.SeckillDao">
    <!-- 目的：为DAO接口方法提供sql语句配置 -->
    <!--
        减库存
        传入多个参数时，可以不写传入参数的类型
     -->
    <update id="reduceNumber">
        update seckill set number=number-1 where seckill_id=#{seckillId}
                                             and start_time <![CDATA[ <= ]]>#{killTime}
                                             and end_time>=#{killTime}
                                             and number>0;
    </update>

    <!-- 根据 seckillId查询产品信息 -->
    <select id="findById" resultType="com.plm.entity.Seckill" parameterType="java.lang.Long">
        select seckill_id,name,number,start_time,end_time,create_time from seckill where seckill_id=#{seckillId};
    </select>

    <!-- 根据偏移量offset 查询秒杀产品列表 -->
    <select id="findAll" resultType="com.plm.entity.Seckill">
        select seckill_id,name,number,start_time,end_time,create_time from seckill
        order by create_time desc limit #{offset},#{limit};
    </select>
</mapper>
```
> mapper/SuccessKilledDao.xml // 映射配置文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.plm.dao.SuccessKilledDao">
    <!-- 目的：为DAO接口方法提供sql语句配置 -->

    <!--
        添加秒杀成功明细
            insert ignore into ===>联合主键的作用，当主键重复报错，证明这个用户已经秒杀过这个产品
     
        状态标识:-1:无效 0:成功 1:已付款 2:已发货   默认是 -1
     -->
    <insert id="insertSuccessKilled">
        insert ignore into success_killed(seckill_id, user_phone) VALUES (#{seckillId},#{userPhone});
    </insert>


    <!--
        根据 seckillId查询秒杀成功的明细，以及所秒杀商品的信息

            通过级联：告诉MyBatis将查询结果映射到SuccessKilled实体类的同时，
            映射到SuccessKilled实体类的seckill属性并进行封装
     -->
    <select id="findByIdWithSeckill" resultType="com.plm.entity.SuccessKilled">
        select sk.seckill_id,sk.user_phone,sk.create_time,sk.state,
               s.seckill_id "seckill.seckill_id",
               s.name "seckill.name",
               s.number "seckill.number",
               s.start_time "seckill.start_time",
               s.end_time "seckill.end_time",
               s.create_time "seckill.create_time"
        from success_killed sk inner join seckill s on sk.seckill_id=s.seckill_id
        where sk.seckill_id=#{seckillId} and sk.user_phone=#{userPhone};
    </select>
</mapper>
```
> > `insert ignore into 目的是避免同一用户在秒杀时间内多次秒杀同一产品`

### MyBatis整合Spring
> 数据库相关参数在 resources/jdbc.properties
```properties
# 数据库相关参数
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=utf-8
username=root
password=root
```
> 无论是Spring还是MyBatis中与Dao相关的配置在 resources/spring/spring-dao.xml 中
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 配置整合MyBatis -->
    <!-- 1、配置数据库相关参数 -->
    <context:property-placeholder location="classpath:jdbc.properties"></context:property-placeholder>

    <!-- 2、数据库连接池 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <!-- 配置连接池属性 -->
        <property name="driverClass" value="${driver}"></property>
        <property name="jdbcUrl" value="${url}"></property>
        <property name="user" value="${username}"></property>
        <property name="password" value="${password}"></property>

        <!-- c3p0连接池的私有属性 -->
        <property name="maxPoolSize" value="30"></property>
        <property name="minPoolSize" value="10"></property>
        <!-- 关闭连接后不自动commit -->
        <property name="autoCommitOnClose" value="false"></property>
        <!-- 获取连接超时时间 -->
        <property name="checkoutTimeout" value="1000"></property>
        <!-- 当获取连接失败重试次数 -->
        <property name="acquireRetryAttempts" value="2"></property>
    </bean>

    <!-- 3、配置SqlSessionFactory对象 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!-- 注入数据库连接池 -->
        <property name="dataSource" ref="dataSource"></property>
        <!-- 配置MyBatis全局配置文件:mybatis-config.xml -->
        <property name="configLocation" value="classpath:mybatis-config.xml"></property>
        <!-- 扫描entity包 使用别名 -->
        <property name="typeAliasesPackage" value="com.plm.entity"></property>
        <!-- 扫描sql配置文件mapper需要的xml文件 -->
        <property name="mapperLocations" value="classpath:mapper/*.xml"></property>
    </bean>

    <!-- 4、配置扫描Dao接口包，动态实现Dao接口，注入到 Spring容器中-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- 注入SqlSessionFactory -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
        <!-- 输出需要扫描的Dao接口包 -->
        <property name="basePackage" value="com.plm.dao"></property>
    </bean>
</beans>
```
## Junit 单元测试Dao
> SeckillDao.java的测试类  
```java
package com.plm.dao;

import com.plm.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * SeckillDao 测试类
 *
 *  配置Spring和 junit整合,junit启动时加载springIOC容器
 */
// junit启动时加载springIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉 junit spring配置文件的位置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    // 注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        int updateNum = seckillDao.reduceNumber(1000L,killTime);
        System.out.println("updateNum = "+updateNum);
    }

    @Test
    public void findById() {
        long id = 1000;
        Seckill seckill = seckillDao.findById(id);
        System.out.println(seckill);
    }

    @Test
    public void findAll() {
        List<Seckill> seckills = seckillDao.findAll(0,100);
        for(Seckill seckill : seckills){
            System.out.println(seckill);
        }
    }
}
```
> /src/test/java/com/plm/dao/SeckillDaoTest.java
```java
package com.plm.dao;

import com.plm.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        long id = 1000L;
        long phone = 12345678901L;
        int insertNum = successKilledDao.insertSuccessKilled(id,phone);
        System.out.println("insertNum = "+insertNum);
    }

    @Test
    public void findByIdWithSeckill() {
        long id = 1000L;
        long phone = 12345678901L;
        SuccessKilled successKilled = successKilledDao.findByIdWithSeckill(id,phone);
        System.out.println(successKilled);
        System.out.println("=========");
        System.out.println(successKilled.getSeckill());
    }
}
```

# Java高并发秒杀API之Web层




