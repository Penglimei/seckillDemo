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
