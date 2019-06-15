package com.dwj.seckill.job;

import com.dwj.seckill.dao.SeckillDao;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import static com.dwj.seckill.constant.RedsKey.Seckill.SECKILL_STOCK;

@Slf4j
public class SyncStockJob extends QuartzJobBean {

    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Long id = dataMap.getLong("id");
        Long stock = redisTemplate.opsForList().size(SECKILL_STOCK + id);
        log.info("执行库存同步：id-->{}" , id);
        log.info("执行库存同步：stock-->{}" , stock);
        int i = seckillDao.updateStock(id, stock);
        log.info("执行库存同步：影响行数-->{}" , i);
    }
}
