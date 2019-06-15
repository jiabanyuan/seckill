package com.dwj.seckill.model;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SeckillUrlResult  implements Serializable {
    //ID
    private Long id;
    //md5
    private String md5;
    //获取结果 0失败  1成功
    private Integer result;
    //描述
    private String msg;
    //服务器当前时间
    private Date now;
    //秒杀开始时间
    private Date startDate;
    //秒杀结束数据
    private Date endDate;
    //秒杀状态 0未开始 1开始  2结束
    private Integer state;
}
