package com.dwj.seckill.service;

import com.dwj.seckill.common.BaseResponse;
import com.dwj.seckill.model.SeckillItemVo;
import com.dwj.seckill.model.SeckillResult;
import com.dwj.seckill.model.SeckillUrlResult;

import java.util.List;

public interface SeckillService {

    /**
     * 获取秒杀商品列表
     * @return
     */
    List<SeckillItemVo> list();

    /**
     * 更改秒杀活动状态
     * @param open
     * @return
     */
    BaseResponse<String> state(Boolean open);

    /**
     * 执行秒杀
     * @param id
     * @param md5
     * @return
     */
    BaseResponse<SeckillResult> seckill(Long id, String md5);

    /**
     * 获取秒杀地址
     * @param id
     * @return
     */
    BaseResponse<SeckillUrlResult> url(Long id);
}
