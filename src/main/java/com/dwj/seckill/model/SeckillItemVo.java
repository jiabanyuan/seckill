package com.dwj.seckill.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeckillItemVo implements Serializable {
    //商品ID
    private Long id;
    //商品图片链接
    private String imgPath;
    //商品名称
    private String itemName;
    //商品描述
    private String itemDesc;
    //商品价格
    private BigDecimal price;
    //库存
    private Integer stock;

    //秒杀地址md5加密
    private String md5;
    //系统时间
    private Date now;
    //开始时间
    private Date startDate;
    //结束时间
    private Date endDate;
    //是否开启秒杀
    private Integer seckillState;
}
