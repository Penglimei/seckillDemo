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
> [Java高并发秒杀API之Service层](https://www.imooc.com/view/631)  
> [Java高并发秒杀API之Web层](https://www.imooc.com/view/630)  
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
                table(create_time)<==>Entity(createTime)
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
> /src/test/java/com/plm/dao/SeckillDaoTest.java  
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
> /src/test/java/com/plm/dao/SuccessKilledDaoTest.java
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

# Java高并发秒杀API之Service层  
## 自定义异常类 
> exception/SeckillException.java   基础的异常类,秒杀相关业务异常  
```java
package com.plm.exception;

/**
 *  秒杀相关业务异常
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

> exception/RepeatKillException.java    重复秒杀异常  
```java
package com.plm.exception;


/**
 *  重复秒杀异常（运行期异常）
 *      避免用户使用 软件进行恶意秒杀或者是无意点击多次秒杀
 *
 *  Mysql只支持运行期异常的回滚操作
 */
public class RepeatKillException extends SeckillException{

    public RepeatKillException (String message){
        super(message);
    }

    public RepeatKillException (String message,Throwable cause){
        super(message,cause);
    }
}

```

> exception/SeckillCloseException.java  秒杀关闭异常，当秒杀结束时用户还要进行秒杀就会出现这个异常  
```java
package com.plm.exception;

/**
 *  秒杀关闭异常，当秒杀结束时用户还要进行秒杀就会出现这个异常
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

## 封装Web层和Service层之间传递的数据的自定义数据传输层类
> dto/Exposer.java  暴露秒杀地址  
```java
package com.plm.dto;

/**
 *  dto:数据传输层
 *
 *  暴露秒杀地址
 */
public class Exposer {

    // 是否开启接口
    private boolean exposed;
    // 一种加密措施
    private String md5;
    private long seckillId;
    // 系统当前时间(毫秒)
    private long now;
    // 秒杀开启时间
    private long start;
    // 秒杀结束时间
    private long end;

    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    public Exposer(boolean exposed, long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", seckillId=" + seckillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
```  

> > `秒杀地址暴露`
> > > + 需要有专门一个方法实现秒杀地址输出，避免人为因素（使用软件恶意获取秒杀地址...）提前知道秒杀地址而出现漏洞。  
> > > + 获取秒杀url时，如果不合法，则返回当前时间和秒杀项目的时间；如果合法，才返回md5加密后url，以避免url被提前获知。  
> > > + 使用md5将url加密、校验，防止秒杀的url被篡改。  

> dto/SeckillExecution.java     封装秒杀执行后的结果  
```java
package com.plm.dto;


import com.plm.entity.SuccessKilled;
import com.plm.enums.SeckillStatEnum;

/**
 *  封装秒杀执行后的结果
 */
public class SeckillExecution {

    private long seckillId;
    // 秒杀执行结果状态
    private int state;
    // 状态信息
    private String stateInfo;
    // 秒杀成功对象
    private SuccessKilled successKilled;

    // 秒杀成功
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    // 秒杀失败
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }
}

```

> enums/SeckillStatEnum.java `用常量枚举类封装秒杀结果返回的state和stateInfo参数信息，方便重复利用，也易于维护`
```java
package com.plm.enums;
/**
 *  使用枚举表述常量数据字段
 */
public enum SeckillStatEnum {

    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATA_REWRITE(-3,"数据篡改");

    private int state;
    private String stateInfo;

    SeckillStatEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SeckillStatEnum stateOf(int index){
        for (SeckillStatEnum statEnum : values()){
            if(statEnum.getState() == index){
                return statEnum;
            }
        }
        return null;
    }
}

```  
> > `SeckillStatEnum statEnum; statEnum.getState();statEnum.getStateInfo();`

## Service层接口设计
> service/SeckillService.java   
```java
package com.plm.service;

import com.plm.dto.Exposer;
import com.plm.dto.SeckillExecution;
import com.plm.entity.Seckill;
import com.plm.exception.RepeatKillException;
import com.plm.exception.SeckillCloseException;
import com.plm.exception.SeckillException;

import java.util.List;

/**
 *  业务接口
 */
public interface SeckillService {

    /**
     *  查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     *  查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     *  秒杀开启时输出秒杀接口地址，
     *  否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     *  执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
            throws SeckillException,RepeatKillException, SeckillCloseException;
}
```

> service/impl/SeckillServiceImpl.java   
```java
package com.plm.service.impl;

import com.plm.dao.SeckillDao;
import com.plm.dao.SuccessKilledDao;
import com.plm.dto.Exposer;
import com.plm.dto.SeckillExecution;
import com.plm.entity.Seckill;
import com.plm.entity.SuccessKilled;
import com.plm.enums.SeckillStatEnum;
import com.plm.exception.RepeatKillException;
import com.plm.exception.SeckillCloseException;
import com.plm.exception.SeckillException;
import com.plm.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    // 注入service依赖
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    // md5盐 ，随便写越复杂越好，用于混淆要加密的值，不易被破解
    private final String slat = "fgvgsdbdHIOUBIYOfwgfw4&*()&^guybl";

    @Override
    public List<Seckill> getSeckillList() {
        List<Seckill> seckills = seckillDao.findAll(0,4);
        return seckills;
    }

    @Override
    public Seckill getById(long seckillId) {
        Seckill seckill = seckillDao.findById(seckillId);
        return seckill;
    }

    @Override
    @Transactional
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.findById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        // 当前系统时间
        Date curTime = new Date();
        // 不能暴露秒杀地址
        if (curTime.getTime() < startTime.getTime() || curTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, curTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 可以暴露秒杀地址
        // 转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    // MD5加密
    private String getMD5(long seckillId){
        String base = seckillId + "/" +slat;
        // base转为二进制进而加密
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    // 秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {

        try {
            // 商品不存在或者是重复秒杀
            if (md5 == null || !md5.equals(getMD5(seckillId))) {
                throw new SeckillException("seckill data rewrite");
            }
            Date curTime = new Date();
            int updateCount = seckillDao.reduceNumber(seckillId, curTime);

            if (updateCount <= 0) { // 秒杀失败，减库存失败
                // 秒杀结束
                throw new SeckillCloseException("seckill is closed");
            } else { // 秒杀成功，减库存成功，增加明细
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    // 重复插入秒杀明细
                    throw new RepeatKillException("seckill repeated");
                } else {
                    // 秒杀成功，增加明细成功
                    SuccessKilled successKilled = successKilledDao.findByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (RepeatKillException e1){ 
            throw e1;
        } catch (SeckillCloseException e2) {
            throw e2;
        }catch (SeckillException e) { // 回滚
            logger.error(e.getMessage(),e);
            throw new SeckillException("seckill inner error : "+e.getMessage());
        }
    }
}
```
> > `在以上代码中，我们捕获了运行时异常，原因是Spring的事务默认是发生了RuntimeException才会回滚，发生了其他异常不会回滚，所以在最后的catch块里通过throw new SeckillException("seckill inner error :"+e.getMessage());将编译期异常转化为运行期异常。`  
> > `调用枚举常量 SeckillStatEnum.SUCCESS `  

## spring托管Service依赖理论
> 业务对象依赖图
> > ![业务对象依赖图](/Users/penglimei/IntelliJ_IDEAProjects/Interview/seckillDemo/src/main/webapp/WEB-INF/pictures/业务对象依赖图.png)

> 本项目中IOC使用
> > XML配置第三方类库 + package-scan扫描加了注解的自定义类service、Dao并注入到Spring容器中 + Annotation注解自定义的Service、Dao类  

> 与Service相关的依赖配置在 spring/spring-service.xml  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 扫描service包下所有使用注解的类型 -->
    <context:component-scan base-package="com.plm.service"></context:component-scan>
    
    <!-- 配置事务管理器 -->
        <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
            <!-- 注入数据库连接池 -->
            <property name="dataSource" ref="dataSource"></property>
        </bean>
    
        <!-- 配置基于注解的声明式事务，默认使用注解来管理事务 -->
        <tx:annotation-driven transaction-manager="transactionManager"></tx:annotation-driven>
</beans>
```
> > `若<property name="dataSource" ref="dataSource"></property>中dataSource报红，但是使用正常，可能是编辑器未检测到注入依赖的问题，不用在意。`

> 使用spring声明式事务管理
> + 本项目使用 注解@Transactional方式声明事务  
> 原因：  
> 1、开发团队达成一致约定，统一标注事务方法的编程风格，不需要再去查编程文档；  
> 2、保证事务方法的执行时间尽可能短，不要穿插其他的网络操作RPC/HTTP请求；  
> 3、不是所有的方法都需要事务，如：只有一条修改操作，只读操作不需要事务。  
> + 抛出运行期异常(RuntimeException)时事务回滚，出现异常不回滚可能会出现事务部分执行的情况  

## service层的集成测试
> resources/logback.xml配置文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>

```

> /src/test/java/com/plm/service/SeckillServiceTest.java  
```java
package com.plm.service;

import com.plm.dto.Exposer;
import com.plm.dto.SeckillExecution;
import com.plm.entity.Seckill;
import com.plm.exception.RepeatKillException;
import com.plm.exception.SeckillCloseException;
import com.plm.exception.SeckillException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list = {}",list);
    }

    @Test
    public void getById() {
        Seckill seckill = seckillService.getById(1000L);
        logger.info("seckill = {}",seckill);
    }

    @Test
    public void exportSeckillUrlAndexecuteSeckill() {
        Exposer exposer = seckillService.exportSeckillUrl(1003L);
        if(exposer.isExposed()){
            logger.info("exposer = {}",exposer);
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(1003L,
                        12345678901L,exposer.getMd5());
                logger.info("seckillExecution = {}",seckillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else {
            // 秒杀未开启
            logger.warn("exposer = {}",exposer);
        }
    }
}
```

# Java高并发秒杀API之Web层




