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
> > ![IDEA中创建Maven项目](https://github.com/Penglimei/seckillDemo/blob/master/src/main/webapp/WEB-INF/pictures/IDEA中创建Maven项目.png)

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        List<Seckill> seckills = seckillDao.findAll(0, 4);
        return seckills;
    }

    @Override
    public Seckill getById(long seckillId) {
        Seckill seckill = seckillDao.findById(seckillId);
        return seckill;
    }

    /**
     * 使用注解控制事务方法的有点：
     * 1、开发团队达成一致约定，统一标注事务方法的编程风格，不需要再去查编程文档；
     * 2、保证事务方法的执行时间尽可能短，不要穿插其他的网络操作RPC/HTTP请求；
     * 3、不是所有的方法都需要事务，如：只有一条修改操作，只读操作不需要事务。
     *
     * @param seckillId
     * @return
     */
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
        return new Exposer(true, md5, seckillId);
    }

    // MD5加密
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
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
            // 增加购买明细
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

            if (insertCount <= 0) { // 看是否该明细被重复插入，即用户是否重复秒杀
                // 重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                // 秒杀成功，减库存成功
                int updateCount = seckillDao.reduceNumber(seckillId,curTime);
                if (updateCount <= 0) {
                    // 重复插入秒杀明细
                    throw new RepeatKillException("seckill is closed");
                } else {
                    // 秒杀成功，增加明细成功
                    SuccessKilled successKilled = successKilledDao.findByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (RepeatKillException e1) {
            throw e1;
        } catch (SeckillCloseException e2) {
            throw e2;
        } catch (SeckillException e) { // 回滚
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error : " + e.getMessage());
        }
        /**
         *  在以上代码中，我们捕获了运行时异常，
         *  原因是Spring的事务默认是发生了RuntimeException才会回滚，
         *  发生了其他异常不会回滚，
         *  所以在最后的catch块里通过
         *  throw new SeckillException("seckill inner error :"+e.getMessage());
         *  将编译期异常转化为运行期异常。
         */
    }
}
```
> > `在以上代码中，我们捕获了运行时异常，原因是Spring的事务默认是发生了RuntimeException才会回滚，发生了其他异常不会回滚，所以在最后的catch块里通过throw new SeckillException("seckill inner error :"+e.getMessage());将编译期异常转化为运行期异常。`  
> > `调用枚举常量 SeckillStatEnum.SUCCESS `  

## spring托管Service依赖理论
> 业务对象依赖图
> > ![业务对象依赖图](https://github.com/penglimei/IntelliJ_IDEAProjects/Interview/seckillDemo/src/main/webapp/WEB-INF/pictures/业务对象依赖图.png)

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
## 前端页面流程  
![前端页面流程](https://github.com/Penglimei/seckillDemo/blob/master/src/main/webapp/WEB-INF/pictures/%E5%89%8D%E7%AB%AF%E9%A1%B5%E9%9D%A2%E6%B5%81%E7%A8%8B.png)

## 详情页流程逻辑  
![详情页流程逻辑](https://github.com/Penglimei/seckillDemo/blob/master/src/main/webapp/WEB-INF/pictures/%E8%AF%A6%E6%83%85%E9%A1%B5%E6%B5%81%E7%A8%8B%E9%80%BB%E8%BE%91.png)
> `获取当前标准系统时间`
## Restful接口
> 一种优雅的URI表述方式、资源的状态和状态转移  
> > 用 /user POST 新增用户 =代替= 原始的 /user/save POST 新增用户  
> > 用 /user PUT 修改用户信息 =代替= 原始的 /user/update POST 修改用户信息  
> > > 每次请求的接口或者地址,都在做描述,例如查询的时候用了 save,新增的时候用了 update,其实完全没有这个必要,我使用了get请求,就是查询.使用post请求,就是新增的请求,我的意图很明显,完全没有必要做描述,这就是为什么有了restful.

> Restful规范:   

 http规范 | 资源操作 | 幂等性 | 安全性
 :-: | :-: | :-: | :-:
 GET | 查询操作 | 是 | 是
 POST | 添加/修改操作| 否 | 否
 PUT | 修改操作| 是 | 否
 DELETE | 删除操作| 是 | 否

### 秒杀API的URL设计
> 秒杀列表  GET /seckill/list  
> 详情页   GET /seckill/{id}/detail  
> 系统时间  GET /seckill/time/now
> 暴露秒杀地址    POST /seckill/{id}/exposer
> 执行秒杀  POST /seckill/{id}/{md5}/execution

## SpringMVC整合Spring
> SpringMVC框架理论  
> > `围绕Handler开发`  

> SpringMVC运行流程  
![SpringMVC运行流程](https://github.com/Penglimei/seckillDemo/blob/master/src/main/webapp/WEB-INF/pictures/SpringMVC运行流程.png)

> 注解映射
> + @RequestMapping注解
> + @ResponseBody注解  

> 请求方法细节处理
> + 请求参数绑定
> + 请求方式绑定
> + 请求转发和重定向
> + 数据模型赋值  
![请求方法细节处理](https://github.com/Penglimei/seckillDemo/blob/master/src/main/webapp/WEB-INF/pictures/%E8%AF%B7%E6%B1%82%E6%96%B9%E6%B3%95%E7%BB%86%E8%8A%82%E5%A4%84%E7%90%861.png)
> + 返回json数据  
![请求方法细节处理返回json数据](https://github.com/Penglimei/seckillDemo/blob/master/src/main/webapp/WEB-INF/pictures/%E8%AF%B7%E6%B1%82%E6%96%B9%E6%B3%95%E7%BB%86%E8%8A%82%E5%A4%84%E7%90%862json.png)
> + cookie访问  
![请求方法细节处理cookie访问](https://github.com/Penglimei/seckillDemo/blob/master/src/main/webapp/WEB-INF/pictures/%E8%AF%B7%E6%B1%82%E6%96%B9%E6%B3%95%E7%BB%86%E8%8A%82%E5%A4%84%E7%90%863cookie.png)

> 配置DispatcherServlet   resources/web.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
		 http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1"
         metadata-complete="true">
    <!--
        <!DOCTYPE web-app PUBLIC
       "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
       "http://java.sun.com/dtd/web-app_2_3.dtd" >


        Servlet2.3 版本中不支持 jsp el 需要将 Servlet修改为更高的版本
        本项目使用 Servlet3.1版本
    -->
    <!-- 配置DispatcherServlet -->
    <servlet>
        <servlet-name>seckill-dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!--
        配置SpringMVC需要加载的配置文件

        spring-dao.xml  spring-service.xml  spring-web.xml
            MyBatis           Spring           SpringMvc
        -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring/spring-*.xml</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>seckill-dispatcher</servlet-name>
        <!-- 默认加载所有的请求 -->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```
> SpringMVC配置文件 spring/spring-web.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!-- 配置SpringMVC -->

    <!--
        1、开启注解模式
            简化配置：
                1）、自动注册
                2）、提供一系列：数据绑定、数字、日期的format  @NumberFormat   @DataTimeFormat
                                xml、json默认读写支持
     -->
    <mvc:annotation-driven></mvc:annotation-driven>

    <!--
        2、静态资源默认servlet配置
            1).加入对静态资源处理：js,gif,png
            2).允许使用 "/" 做整体映射
     -->
    <mvc:default-servlet-handler></mvc:default-servlet-handler>

    <!-- 3、配置jsp 显示ViewResolver -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"></property>
        <property name="prefix" value="/WEB-INF/jsp/"></property>
        <property name="suffix" value=".jsp"></property>
    </bean>
    <!-- 4、扫描 web(controller) 相关的bean -->
    <context:component-scan base-package="com.plm.web"></context:component-scan>
</beans>
```

> web/SeckillController.java
```java
package com.plm.web;


import com.plm.dto.Exposer;
import com.plm.dto.SeckillExecution;
import com.plm.dto.SeckillResult;
import com.plm.entity.Seckill;
import com.plm.enums.SeckillStatEnum;
import com.plm.exception.RepeatKillException;
import com.plm.exception.SeckillCloseException;
import com.plm.exception.SeckillException;
import com.plm.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;


    /**
     *  查看所有秒杀商品
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        // 获取列表
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        //  /WEB-INF/jsp/list.jsp
        return "list";
    }


    /**
     * 查看某一秒杀商品的详细信息
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     *  暴露秒杀地址
     * @param seckillId
     * @return
     */
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody // springmvc看到这个注解时会将 exposer()的返回值封装为json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            // 请求成功
            result = new SeckillResult<>(true,exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            // 请求失败
            result = new SeckillResult<>(false,e.getMessage());
        }
        return result;
    }

    /**
     *  执行秒杀
     * @param seckillId
     * @param phone 从用户浏览器中名为killPhone的cookie中获取的，
     *              但是存在一个问题当浏览器中没有名为killPhone的cookie时，springmvc会报错，
     *              所以将其设为非必须的，即对名为killPhone的cookie的验证逻辑放在程序中处理，而不是直接报错.
     * @param md5
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @CookieValue(value = "killPhone",required = false) Long phone,
                                                   @PathVariable("md5") String md5){
        // 对名为killPhone的cookie的验证逻辑放在程序中处理，而不是直接报错
        if (phone == null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }

        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId,phone,md5);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (RepeatKillException e1) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (SeckillCloseException e2) {
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (SeckillException e) {
            logger.error(e.getMessage(),e);
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
    }

    /**
     *  获取系统当前的时间
     * @return
     */
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> getCurTime(){
        Date curTime = new Date();
        return new SeckillResult(true,curTime.getTime());
    }
}
```

> > `Service层中的抛出异常是为了让Spring能够回滚，Controller层中捕获异常是为了将异常转换为对应的Json供前台使用，缺一不可。`

> dto/SeckillResult.java 所有ajax请求返回类型，封装 json结果
```java
package com.plm.dto;

/**
 *  所有ajax请求返回类型，封装 json结果
 */
public class SeckillResult<T> {

    // 指页面是否发送请求成功
    private boolean success;
    private T data;
    private String error;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
```

## 基于BootStrap开发前端页面
> IDEA使用BootStrap的方式有两种  
> 1、下载BootStrap前端组件库；  
> 2、使用BootStrap CDN加速文件，但是这种使用的时候必须联网。  
> 本项目使用CDN加速文件

> jsp/common/head.jsp   单独提取出来相同的头部
```jsp
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta charset="UTF-8">
<!-- 引入 Bootstrap -->
<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet">

<!-- HTML5 Shiv 和 Respond.js 用于让 IE8 支持 HTML5元素和媒体查询 -->
<!-- 注意： 如果通过 file://  引入 Respond.js 文件，则该文件无法起效果 -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
<script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
<![endif]-->
```

> jsp/common/tag.jsp   共用的jstl 
```jsp
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
``` 

> jsp/list.jsp
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- 引入共用的jstl --%>
<%@include file="common/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀列表页</title>
    <%-- 头部都是相同的，因此单独提取出来，放到head.jsp中 --%>
    <%@include file="common/head.jsp" %>
</head>
<body>
<%-- 页面显示部分 --%>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading text-center">
            <h2>秒杀列表</h2>
        </div>
        <div class="panel-body">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th>名称</th>
                    <th>库存</th>
                    <th>开始时间</th>
                    <th>结束时间</th>
                    <th>创建时间</th>
                    <th>详情页</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="sk" items="${list}">
                    <tr>
                        <td>${sk.name}</td>
                        <td>${sk.number}</td>
                        <td>
                            <fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
                        </td>
                        <td>
                            <a class="btn btn-info" href="/seckill/${sk.seckillId}/detail" target="_blank">查看</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
<!-- jQuery (Bootstrap 的 JavaScript 插件需要引入 jQuery) -->
<script src="https://code.jquery.com/jquery.js"></script>
<!-- 包括所有已编译的插件 -->
<script src="js/bootstrap.min.js"></script>
</html>
```

> /webapp/resources/script/seckill.js   存放主要交互逻辑js代码
```js
// 存放主要交互逻辑js代码 模块化编写

var seckill = {
    // 封装秒杀相关ajax的url
    URL:{
        now: function () {
            return '/seckill/time/now';
        },

        exposer: function (seckillId) {
            return '/seckill/'+seckillId+'/exposer';
        },

        execution: function (seckillId,md5) {
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },

    // 验证手机号
    validatePhone: function(phone){
        if(phone && phone.length==11 && !isNaN(phone)){
            return true;
        }else {
            return false;
        }
    },

    // 执行秒杀
    handlerSeckill: function(seckillId,node){
        // 获取秒杀地址，控制显示器，执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');

        $.get(seckill.URL.exposer(seckillId),{},function (result) {
            // 在回调函数执行交互流程
            if(result && result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){
                    // 开启秒杀 获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId,md5);
                    console.log("killUrl: "+killUrl);
                    // 绑定一次点击事件
                    $('#killBtn').one('click',function () {
                        // 执行秒杀请求
                        // 1、先禁用按钮
                        $(this).addClass('disabled');//this == #killBtn
                        // 2、发送秒杀请求执行秒杀
                        $.post(killUrl,{},function (result) {
                            if(result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                // 显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                }else {
                    // 由于设备存在时间偏移可能存在未开启秒杀的
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    // 重新开始秒杀
                    seckill.countDown(seckillId,now,start,end);
                }
            }else {
                console.log('result: '+result);
            }
        });
    },

    // 时间判断 计时交互
    countDown: function(seckillId,nowTime,startTime,endTime){
        console.log(seckillId+'_'+nowTime+'_'+startTime+'_'+endTime);
        var seckillBox = $('#seckill-box');
        // 秒杀已经结束
        if(nowTime > endTime){
            seckillBox.html('秒杀结束！');
        }else if (nowTime < startTime){
            // 秒杀还未开始，计时事件绑定
            var killTime = new Date(startTime+1000); // +1秒，是为了防止时间偏移
            seckillBox.countdown(killTime,function (event) {
                // 时间格式化
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown',function () {
                // 当倒计时到达秒杀时间，回调事件，获取秒杀地址，控制实现逻辑，执行秒杀
                console.log('____finish.countdown');
                seckill.handlerSeckill(seckillId,seckillBox);
            });
        } else {
            // 秒杀开始
            seckill.handlerSeckill(seckillId,seckillBox);
        }
    },

    // 详情页秒杀逻辑
    detail:{
        // 详情页初始化
        init: function (params) {
            // 手机验证和登录
            // 在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            // 验证手机号
            if(!seckill.validatePhone(killPhone)) {
                // 绑定手机号    控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true, // 显示弹出层
                    backdrop: 'static', // 禁止位置关闭
                    keyboard: false // 关闭键盘事件
                });

                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log("inputPhone===="+inputPhone);
                    if (seckill.validatePhone(inputPhone)) {
                        // 电话写入cookie(7天过期)
                        $.cookie('killPhone',inputPhone,{expires: 7,path: '/seckill'});
                        // 验证通过 刷新页面
                        window.location.reload();
                    }else {
                        // 错误文案信息实际项目中应该抽取到前端字典里
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);
                    }
                });
            }


            // 已经登陆
            // 规划交互流程，计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            console.log("开始秒杀时间===="+startTime);
            console.log("结束秒杀时间===="+endTime);
            $.get(seckill.URL.now(),{},function (result) {
                // 请求成功
                if(result && result['success']){
                    var nowTime = result['data'];
                    // 时间判断 计时交互
                    seckill.countDown(seckillId,nowTime,startTime,endTime);
                }else {
                    console.log('result: '+result);
                    alert('result: '+result);
                }
            });
        }
    }
};
```

> jsp/detail.jsp
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <%@include file="common/head.jsp"%>
</head>
<body>

<div class="container">
    <div class="panel panel-default text-center">
        <div class="panel-heading">
            <h1>${seckill.name}</h1>
        </div>

        <div class="panel-body">
            <h2 class="text-danger">
                <%-- 显示time图标 --%>
                <span class="glyphicon glyphicon-time"></span>
                <%-- 展示倒计时 --%>
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>
</div>

<%-- 登陆弹出层  输入电话--%>
<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>秒杀电话：
                </h3>
            </div>

            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killPhone" id="killPhoneKey" placeholder="填写手机号" class="form-control">
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <%--验证信息--%>
                <span id="killPhoneMessage" class="glyphicon"></span>
                <button type="button" id="killPhoneBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-phone"></span>
                    Submit
                </button>
            </div>
        </div>
    </div>
</div>

</body>
<%--jQery文件,务必在bootstrap.min.js之前引入--%>
<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<%--使用CDN 获取公共js http://www.bootcdn.cn/--%>
<%--jQuery Cookie操作插件--%>
<script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<%--jQuery countDown倒计时插件--%>
<script src="https://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
<%--开始编写交互逻辑--%>
<script src="/resources/script/seckill.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {
        // 使用EL表达式传入参数
        seckill.detail.init({
            seckillId : ${seckill.seckillId},
            startTime : ${seckill.startTime.time},// 毫秒
            endTime : ${seckill.endTime.time}
        });
    });
</script>
</html>
```

# Java高并发秒杀API之高并发优化
## 高并发分析
> 并发  
  无论是单核还是多核,`允许多个任务同时存在`，多个任务交替使用CPU，`同一时刻只能运行一个进程`。  
>  + 本项目中的并发的体现：  
  多个线程同时访问同一行数据，产生事务，继而产生行锁，
  `只有占有锁的线程释放锁后，等待锁资源的线程才有机会获取锁`。
  `每秒执行率(QPS)与事务执行的时间有密切关系，事务执行时间越短，并发性越高`。
  数据库mysql中的数据存储在磁盘上，每执行一次数据操作都需要磁盘I/O，耗时，
  因此，`要提高系统的性能，就需要尽可能减少磁盘I/O`。  
  
> 存在高并发的地方：
![高并发分析](/Users/penglimei/IntelliJ_IDEAProjects/Interview/seckillDemo/src/main/webapp/WEB-INF/pictures/高并发分析.png)


> + 详情页  
    秒杀还未开始，用户大量刷新详情页面  
>  + 将静态化详情页和一些静态资源(css,js等)放置到CDN上进行优化，降低服务器压力    
> > `CDN的理解`：  
    其目的是通过在现有的Internet中增加一层新的网络架构，将网站的内容发布到最接近用户的网络节点上，
    使用户可以就近取得所需的内容，`解决Internet网络拥塞状况，提高用户访问网站的响应速度`。
    从技术上全面解决由于网络带宽小、用户访问量大、网点分布不均等原因，
    `解决用户访问网站的响应速度慢的根本原因，适合请求不变的资源`。  
    CDN能够为网络的快速、安全、稳定、可扩展等方面提供保障。  
    一般在CDN获取数据后不需要访问后端服务器。  
> > `存在的问题`：  
    请求都发送到CDN上，无法获取到系统当前的时间，进而无法进行秒杀时间段的控制  
    
> + 系统时间  
    由于将静态化详情页和一些静态资源(css,js等)放置到CDN上，无法获取当前系统的时间，
    所以，需要单独设计一个请求来获取服务器的系统时间，当大量用户请求时存在高并发现象。
    但是`获取系统时间不需要优化，获取系统时间即new Date();对象返回给用户，
    不考虑GC影响，一个Date对象一秒钟可以获取一亿次系统时间，没有后端访问`。  
    
> + 地址暴露接口  
    秒杀地址的返回数据是变化的，不适合用CDN缓存，秒杀开始瞬间大量用户请求秒杀地址，
    存在高并发。  
>  + `适合用服务端缓存redies等优化`，这些缓存一秒钟可以承受10万`qps(每秒查询率) ` 
    流程：  
    用户请求秒杀地址，redis+mysql一致性维护，
    即先向redis请求，当redis没有数据(缓存穿透)或请求超时时，
    向mysql发生请求获取数据，或者mysql主动更新缓存。

> + 执行秒杀操作  
>  + 存在的问题：  
        1、无法使用CDN缓存，执行秒杀是核心代码，且数据变动；  
        2、后端缓存困难，存在双写不一致问题，库存问题；  
        3、mysql中某一行数据可能成为热点数据进行大量的update set，热点商品存在竞争。
>  + 如何判断update执行库存成功：  
        1、update sql语句自身没有报错；  
        2、确认update sql语句影响记录条数。

>  + 优化方案：  
    使用存储过程：整个事务在 Mysql 端完成，用存储过程写业务逻辑，服务端负责调用。  

>  + 优化分析：  
       1、 `行级锁在commit之后释放-》优化方向减少行级锁持有时间`  
       2、 `使用存储过程，整个事务在mysql端完成，避免网络延迟和GC影响`     

## redis优化---后端"地址暴露接口"缓存  
> 主机上下载安装reds  
> > + 查看版本号
> > ```
> > redis-server -v // 服务器端 Redis server v=5.0.4
> > redis-cli -v    // 客户端  redis-cli 5.0.4
> > ```
> > + 启动redis
> > ```
> > redis-server    // 启动redis服务端，并监听6379端口
> > redis-cli shutdown  // 服务端会断开所有客户端的连接，然后根据配置执行持久化，最后退出
> > Ctrl+c  // 退出redis server服务
> > redis-cli -h 127.0.0.1 -p 6379  // 指定地址和端口启动redis客户端
> > quit    // 在交互模式关闭客户端
> > ```
> > + 查看redis中是否存在数据
> > ```
> > dbsize  // 返回redis中存在的keys
> > keys *  // 列举所有keys,查看缓存整体结构
> > type key    // 获取key的类型
> > get key    // 获取key的详细信息
> > ```

> 引入redis依赖+protostuff序列化依赖 pom.xml 
```xml
     <!-- redis 客户端: Jedis -->
     <dependency>
       <groupId>redis.clients</groupId>
       <artifactId>jedis</artifactId>
       <version>2.7.0</version>
     </dependency>
 
     <!-- protostuff序列化依赖 -->
     <dependency>
       <groupId>com.dyuproject.protostuff</groupId>
       <artifactId>protostuff-core</artifactId>
       <version>1.1.5</version>
     </dependency>
     <dependency>
       <groupId>com.dyuproject.protostuff</groupId>
       <artifactId>protostuff-runtime</artifactId>
       <version>1.1.5</version>
     </dependency>
```

> redis存储缓存数据操作 dao/cache/RedisDao.java
```java
package com.plm.dao.cache;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.plm.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis缓存数据
 *
 *  import java.io.Serializable;是源生JDK序列化
 *
 *      protostuff 序列化相比 Serializable序列化，
 *      可将占有空间压缩到原先的1/10～1/5，
 *      且压缩速度比原先的快两个数量级，更节省CPU。
 */
public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JedisPool jedisPool;
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip,int port) {
        jedisPool = new JedisPool(ip,port);
    }

    public Seckill getSeckill(long seckillId){
        // redis 操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckillId;
                // redis,jedis并没有实现内部序列化操作，因此在存取对象时针对二进制数组操作
                // get->byte[]->反序列化->Object(Seckill)
                // 采用自定义的序列化
                byte[] bytes = jedis.get(key.getBytes());
                // 从缓存中获取到seckillId对应的字节数组
                if(bytes != null){
                    // 空对象
                    Seckill seckill = schema.newMessage();
                    // 用 protostuff 通过schema将bytes传到seckill
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    // seckill 反序列化
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill){
        // set Object(Seckill)->序列化->byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                // 超时缓存
                int timeout = 60*60; //1小时
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
```

> > `采用开源社区中protostuff实现自定义序列化，
     相比 Serializable源生JDK序列化，
     可将占有空间压缩到原先的1/10～1/5，
     且压缩速度比原先的快两个数量级，
     更节省CPU、提高性能。`  
> > `本项目中 redis 的key 使用的是String数据类型`

> 在 spring-dao.xml中配置redisDao.java
```xml
<!-- RedisDao -->
    <bean id="redisDao" class="com.plm.dao.cache.RedisDao">
        <!-- ip -->
        <constructor-arg index="0" value="localhost"></constructor-arg>
        <!-- port -->
        <constructor-arg index="1" value="6379"></constructor-arg>
    </bean>
```

> 测试 RedisDao.java中的方法，com.plm.dao.cache.RedisDaoTest.java  
```java
package com.plm.dao.cache;

import com.plm.dao.SeckillDao;
import com.plm.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    private long seckillId = 1000L;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void getAndPutSeckill() {
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            seckill = seckillDao.findById(seckillId);
            if(seckill != null){
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill = redisDao.getSeckill(seckillId);
                System.out.println(seckill);
            }
        }
    }
}
```

> 优化SeckillServiceImpl.java中的秒杀暴露接口 exportSeckillUrl()
```java
package com.plm.service.impl;

import com.plm.dao.SeckillDao;
import com.plm.dao.SuccessKilledDao;
import com.plm.dao.cache.RedisDao;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private RedisDao redisDao;

    @Override
    public List<Seckill> getSeckillList() {
        List<Seckill> seckills = seckillDao.findAll(0, 4);
        return seckills;
    }

    @Override
    public Seckill getById(long seckillId) {
        Seckill seckill = seckillDao.findById(seckillId);
        return seckill;
    }

    /**
     * 使用注解控制事务方法的有点：
     * 1、开发团队达成一致约定，统一标注事务方法的编程风格，不需要再去查编程文档；
     * 2、保证事务方法的执行时间尽可能短，不要穿插其他的网络操作RPC/HTTP请求；
     * 3、不是所有的方法都需要事务，如：只有一条修改操作，只读操作不需要事务。
     *
     * @param seckillId
     * @return
     */
    @Override
    @Transactional
    public Exposer exportSeckillUrl(long seckillId) {
        // 优化点：缓存优化
        // 1、访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            // 2、访问数据库
            seckill = seckillDao.findById(seckillId);
            if (seckill == null){
                return new Exposer(false,seckillId);
            }else {
                // 3、放入redis
                redisDao.putSeckill(seckill);
            }
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
        return new Exposer(true, md5, seckillId);
    }

    // MD5加密
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
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
            // 增加购买明细
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

            if (insertCount <= 0) { // 看是否该明细被重复插入，即用户是否重复秒杀
                // 重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                // 秒杀成功，减库存成功
                int updateCount = seckillDao.reduceNumber(seckillId,curTime);
                if (updateCount <= 0) {
                    // 重复插入秒杀明细
                    throw new RepeatKillException("seckill is closed");
                } else {
                    // 秒杀成功，增加明细成功
                    SuccessKilled successKilled = successKilledDao.findByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (RepeatKillException e1) {
            throw e1;
        } catch (SeckillCloseException e2) {
            throw e2;
        } catch (SeckillException e) { // 回滚
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error : " + e.getMessage());
        }
        /**
         *  在以上代码中，我们捕获了运行时异常，
         *  原因是Spring的事务默认是发生了RuntimeException才会回滚，
         *  发生了其他异常不会回滚，
         *  所以在最后的catch块里通过
         *  throw new SeckillException("seckill inner error :"+e.getMessage());
         *  将编译期异常转化为运行期异常。
         */
    }
}
```

> 测试 SeckillServiceImpl.java中的exportSeckillUrl()方法(加断点测试)
```java
@Test
    public void exportSeckillUrlAndexecuteSeckill() {
        long seckillId = 1000L;
        long userPhone = 12345678901L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            logger.info("exposer = {}",exposer);
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId,
                        userPhone,exposer.getMd5());
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
```
> > `没有经过mysql数据库就可以直接获取数据(因为redis中已经存储了)`

## 执行秒杀---并发优化
> 简单优化  
> 优化后，`insert ignore sql语句如果返回结果是0，说明存在重复秒杀，
   不再执行后续的 update操作，可以减少行级锁持有时间。`  
![事务优化前](Users/penglimei/IntelliJ_IDEAProjects/Interview/seckillDemo/src/main/webapp/WEB-INF/pictures/优化前事务执行.png)![事务优化后](/Users/penglimei/IntelliJ_IDEAProjects/Interview/seckillDemo/src/main/webapp/WEB-INF/pictures/优化后事务执行.png)
> 代码优化 SeckillServiceImpl.java 的 executeSeckill()方法
```java
    private String getMD5(long seckillId) {
            String base = seckillId + "/" + slat;
            // base转为二进制进而加密
            String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
            return md5;
    }
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {

        try {
            // 商品不存在或者是重复秒杀
            if (md5 == null || !md5.equals(getMD5(seckillId))) {
                throw new SeckillException("seckill data rewrite");
            }
            Date curTime = new Date();
            // 增加购买明细
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

            if (insertCount <= 0) { // 看是否该明细被重复插入，即用户是否重复秒杀
                // 重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                // 秒杀成功，减库存成功
                int updateCount = seckillDao.reduceNumber(seckillId,curTime);
                if (updateCount <= 0) {
                    // 重复插入秒杀明细
                    throw new RepeatKillException("seckill is closed");
                } else {
                    // 秒杀成功，增加明细成功
                    SuccessKilled successKilled = successKilledDao.findByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (RepeatKillException e1) {
            throw e1;
        } catch (SeckillCloseException e2) {
            throw e2;
        } catch (SeckillException e) { // 回滚
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error : " + e.getMessage());
        }
    }
```

> 1、`插入是可以并行的，而更新由于会加行级锁是串行的。`  
> 2、`更新在前，加锁和释放锁之间有两次网络延迟和GC，
    如果插入在前，加锁和释放锁之间只有一次的网络延迟和GC，也就是减少的持有锁的时间。`  
> 3、`先insert并不会忽略库存不足的情况，因为insert和update是在同一个事务里，
    仅有insert并不一定会提交，只有在update也成功后才会提交，
    所以并不会造成过量插入秒杀成功记录的现象。`
<center>  
<figure>  
<img src="/Users/penglimei/IntelliJ_IDEAProjects/Interview/seckillDemo/src/main/webapp/WEB-INF/pictures/优化前事务执行.png"/>

<img src="/Users/penglimei/IntelliJ_IDEAProjects/Interview/seckillDemo/src/main/webapp/WEB-INF/pictures/优化后事务执行.png"/>
</figure>  
</center>  

> 深度优化  
> > 简单优化存在的问题是：  
    依然存在着`Java客户端和服务器通信时的网络延迟和GC影响`。  
> > 解决办法：  
    可以将执行秒杀操作时的`insert和update放到MySQL服务端的存储过程里`，
    而`Java客户端直接调用这个存储过程`，这样就可以避免网络延迟和可能发生的GC影响。  
    
> > 存储过程：  
> > 是为了完成特定功能的`SQL语句集，经编译创建并保存在数据库中`，
    用户可通过指定存储过程的名字并给定参数(需要时)来调用。  

> > 编写存储过程sql文件 /sql/executeSeckill.sql
```mysql
-- 执行秒杀 存储过程

-- DELIMITER $$  声明语句结束符，可以自定义，即在存储过程中用 $$ 代替 ; 表示语句结束
-- CREATE PROCEDURE demo_in_parameter(IN p_in int)   声明存储过程
-- BEGIN .... END    存储过程开始和结束符
-- IN 输入参数   OUT 输出参数    INOUT 输入输出参数：既表示调用者向过程传入值，又表示过程向调用者传出值
-- DECLARE variable_name [,variable_name...] datatype [DEFAULT value];   局部变量声明,一定要放在存储过程体的开始
-- call sp_name[(传参)];  调用存储过程
-- SET 变量名 = 表达式值 [,variable_name = expression ...] 变量赋值
-- row_count()返回上一条修改类型sql语句(delete、insert、update)的影响行数

delimiter $$

create procedure `seckill`.`execute_seckill`
    (in v_seckill_id bigint, in v_phone bigint,
    in v_kill_time timestamp, out r_result int)

begin
    declare insert_count int default 0;
    start transaction ;
    insert ignore into success_killed (seckill_id, user_phone, create_time) values (v_seckill_id, v_phone, v_kill_time);
    select row_count() into insert_count;
    if (insert_count = 0) then
        rollback ;
        set r_result = -1; -- 重复提交秒杀信息
    elseif (insert_count < 0) then
        rollback ;
        set r_result = -2; -- sql语句错误/未执行
    else
        update seckill set number = number-1
        where seckill_id = v_seckill_id
          and end_time > v_kill_time
          and start_time < v_kill_time
          and number > 0;
        select row_count() into insert_count;
        if (insert_count = 0) then
            rollback ;
            set r_result = 0; --  重复秒杀
        elseif (insert_count < 0) then
            rollback ;
            set r_result = -2; -- sql语句错误/未执行
        else
            commit ;
            set r_result = 1; -- 更新秒杀信息+减少库存事务提交成功
        end if;
    end if;
end $$
-- 存储过程定义结束

-- 查看存储过程的详细
show create procedure seckill.execute_seckill;

-- 删除一个存储过程
drop procedure execute_seckill;

delimiter ;
-- 定义变量
set @r_result = -3;
-- 调用存储过程
call execute_seckill(1001,24563474523,now(),@r_result);
-- 获取结果
select @r_result;
```

> > 代码调用存储过程  
> > 接口dao.SeckillDao.java 中定义调用存储过程的方法 killByProcedure()
```java
/**
     * 使用存储过程执行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String,Object> paramMap);
```
> > mapper/SeckillDao.xml 中实现mybtis调用存储过程的方法 killByProcedure()
```xml
<!-- mybtis调用存储过程 -->
    <select id="killByProcedure" statementType="CALLABLE">
        call execute_seckill(
            #{seckillId,jdbcType=BIGINT,mode=IIN},
            #{phone,jdbcType=BIGINT,mode=IN},
            #{killTime,jdbcType=TIMASTAMP,mode=IIN},
            #{result,jdbcTYPE=INTEGER,model=OUT}
            );
    </select>
```
> > 在接口service.SeckillService.java 中定义方法 executeSeckillProcedure()
```java
/**
     * 执行秒杀操作----通过调用存储过程实现
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckillProcedure (long seckillId,long userPhone,String md5);
```
> > 在pom.xml中添加工具类 mapUtils的依赖 
```xml
<!-- mapUtils依赖 -->
    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-collections4</artifactId>
      <version>4.4</version>
    </dependency>
```
> > 在service.impl.SeckillServiceImp.java实现方法 executeSeckillProcedure()
```java
@Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5==null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        // 执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map,"result",-2);
            if(result == 1){
                SuccessKilled successKilled = successKilledDao.findByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,successKilled);
            }else {
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }
    }
```
> > 在 service.SeckillServiceTest.java 中测试executeSeckillProcedure().java 
```java
@Test
    public void executeSeckillProcedure(){
        long seckillId= 1001;
        long phone = 1234565362;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
            logger.info(execution.getStateInfo());
        }
    }
```
> > 修改 web.SeckillController.java 通过调用存储过程实现秒杀
```java
@RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> executeProcedure(@PathVariable("seckillId") Long seckillId,
                                                   @CookieValue(value = "killPhone",required = false) Long phone,
                                                   @PathVariable("md5") String md5){
        // 对名为killPhone的cookie的验证逻辑放在程序中处理，而不是直接报错
        if (phone == null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }

        try {
            // 调用存储过程执行秒杀seckillService.executeSeckillProcedure
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (RepeatKillException e1) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (SeckillCloseException e2) {
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (SeckillException e) {
            logger.error(e.getMessage(),e);
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
    }
```

> > 启动tomcat，浏览器输入http://localhost:8080/seckill/list 依次测试项目功能

> > `经过简单优化和深度优化之后，本项目大概能达到一个秒杀单6000qps（慕课网视频中张老师说的），
    这个数据对于一个秒杀商品来说其实已经挺ok了，
    注意这里是指同一个秒杀商品6000qps(每秒查询率)，如果是不同商品不存在行级锁竞争的问题。`

## 压测工具
