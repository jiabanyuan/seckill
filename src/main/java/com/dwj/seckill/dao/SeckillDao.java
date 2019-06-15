package com.dwj.seckill.dao;

import com.dwj.seckill.dao.entity.SeckillItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SeckillDao {

    /**
     * 查询列表
     *
     * @return
     */
    List<SeckillItem> list();

    /**
     * 减库存
     * @param id
     * @param num
     * @return
     */
    int reduceStock(@Param("id") Long id, @Param("num") int num);

    /**
     * 更新库存
     * @param id
     * @param stock
     * @return
     */
    int updateStock(@Param("id") Long id,@Param("stock") Long stock);
}
