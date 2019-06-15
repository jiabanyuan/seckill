package com.dwj.seckill.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeckillItem implements Serializable {
    private Long id;
    private String imgPath;
    private String itemName;
    private String itemDesc;
    private BigDecimal price;
    private Integer stock;
    private Date startDate;
    private Date endDate;
}

