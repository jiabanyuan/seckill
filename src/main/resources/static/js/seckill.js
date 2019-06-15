//页面初始化
window.onload = function () {
    var isOpen = $("#isOpen").text();
    if (isOpen == 'true') {
        //TODO 初始化秒杀倒计时
        var $id = $("p[id$='_id']");
        $id.each(function () {
            var id = $(this).text();
            var now = $('#'+id+ '_now').text();
            var start = $('#'+id+ '_startDate').text();
            var end = $('#'+id+ '_endDate').text();
            var state = $('#'+id+ '_state').text();
            //转时间戳
            var nowTime = Date.parse(new Date(now));
            var startTime = Date.parse(new Date(start));
            var endTime = Date.parse(new Date(end));
            if(state && state == 0){
                seckillCountDown(id,nowTime,startTime);
            }else if(state && state == 1){
                seckillEndCountDown(id,nowTime,endTime);
            }
        })
    } else {
        alert("秒杀活动暂未开始");
    }
};

//执行秒杀
function seckill(id) {
    var id = id;
    var md5 = $("#" + id + "_md5").text();
    $.ajax({
        type: "POST",
        url: " http://localhost:8080/seckill/" + id + "/" + md5 + "/execute",
        dataType: "json",
        success: function (result) {//响应成功
            if (result.code == 200) {
                console.log(result);
                alert(result.data.msg);
                if (result.data.result == 1) {//秒杀成功
                    //TODO 更新页面库存数据
                    $("#" + result.data.id + "_stock").text("库存：" + result.data.stock);
                } else if (result.data.result == 0 && result.data.state == 0) {//秒杀失败
                    //TODO 更新时间计数器
                    var nowTime = Date.parse(new Date(result.data.now));
                    var startTime = Date.parse(new Date(result.data.startDate));
                    seckillCountDown(id,nowTime,startTime);
                }
            } else {
                console.log(result);
                alert(result.data.msg);
            }
        },
        error: function (e) {//异常情况
            console.log("Error: " + e.status);
        }
    });
}

//获取秒杀地址
function seckillUrl(id) {
    $.ajax({
        type: "GET",
        url: " http://localhost:8080/seckill/" + id + "/url",
        dataType: "json",
        success: function (result) {
            if (result.code == 200) {//响应成功
                console.log(result);
                var nowTime = Date.parse(new Date(result.data.now));
                var startTime = Date.parse(new Date(result.data.startDate));
                var endTime = Date.parse(new Date(result.data.endDate));
                if (result.data.result == 1) {//获取地址成功
                    var md5 = result.data.md5;
                    if(md5){
                        //TODO 开始结束倒计时
                        seckillEndCountDown(id,nowTime,endTime);
                        //TODO 点亮按钮
                        ableSeckillBtn(id,md5);
                    }
                } else if (result.data.result != 1 && result.data.state == 0) {//秒杀未开始
                    //TODO 更新时间计数器
                    seckillCountDown(id,nowTime,startTime);
                }
            } else {
                console.log(result);
                alert(result.data.msg);
            }
        },
        error: function (e) {//异常情况
            console.log("Error: " + e.status);
        }
    });
}

//点亮秒杀按钮,填充秒杀地址参数
function ableSeckillBtn(id,md5){
    //TODO 更新页面秒杀地址
    $("#" + id + "_md5").text(md5);
    //TODO 点亮秒杀按钮
    $("#" + id + "_btn_a").attr('class', 'btn btn-primary');
    $("#" + id + "_btn_spen").text(" 秒杀");
}

//关闭秒杀按钮,删除秒杀地址参数
function disableSeckillBtn(id){
    //TODO 更新页面秒杀地址
    $("#" + id + "_md5").text('');
    //TODO 关闭秒杀按钮
    $("#" + id + "_btn_a").attr('class', 'btn btn-primary disabled');
    $("#" + id + "_btn_spen").text(" 已结束");
}

//秒杀开始倒计时
function seckillCountDown(id,nowTime,startTime) {
    var timeBox = $("#"+id+"_start_time");
    //以服务器时间为准  秒杀开始时间 = 客户端时间 + (服务器秒杀开始时间 - 服务器当前时间)
    var seckillTime =  new Date().getTime() + (startTime - nowTime);
    timeBox.countdown(seckillTime,function (event) {
        var seckillTimeStr = event.strftime("倒计时：%D天 %H时 %M分 %S秒 开始");
        timeBox.text(seckillTimeStr);
    }).on('finish.countdown',function () {//倒计时完成回调事件
        timeBox.attr('hidden', true);
        $("#"+id+"_end_time").attr('hidden', false);
        //TODO 获取秒杀地址
        seckillUrl(id);
    })

}

//秒杀结束倒计时
function seckillEndCountDown(id,nowTime,endTime){
    var timeBox = $("#"+id+"_end_time");
    //以服务器时间为准  秒杀结束时间 = 客户端时间 + (服务器秒杀结束时间 - 服务器当前时间)
    var seckillEndTime =  new Date().getTime() + (endTime - nowTime);
    timeBox.countdown(seckillEndTime,function (event) {
        var seckillEndTimeStr = event.strftime("倒计时：%D天 %H时 %M分 %S秒 结束");
        timeBox.text(seckillEndTimeStr);
    }).on('finish.countdown',function () {//倒计时完成回调事件
        //TODO 关闭秒杀按钮
        timeBox.text('');
        disableSeckillBtn(id);
    })
}
