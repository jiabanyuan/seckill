package com.dwj.seckill.service;

public interface SeckillTaskService {

    /**
     * 定时任务实现异步数据库减库存(立刻执行,并发大情况不推荐使用)
     * @param id
     * @param num
     * @return
     */
    boolean reduceStock(Long id, int num);

    /**
     * 数据库同步Redis库存 (默认一分钟一次)
     * @param id
     * @return
     */
    boolean syncStock(Long id);

    /**
     * 关闭库存同步任务
     * @param id
     * @return
     */
    boolean syncStockShutDown(Long id);
}
