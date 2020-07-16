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


