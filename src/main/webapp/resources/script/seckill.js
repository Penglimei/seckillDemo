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