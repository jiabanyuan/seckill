package com.dwj.seckill.job;

import com.dwj.seckill.dao.SeckillDao;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class ReduceStockJob extends QuartzJobBean {

    @Autowired
    private SeckillDao seckillDao;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        long id = dataMap.getLong("id");
        int num = dataMap.getInt("num");
        log.info("执行数据库减库存：id-->{}" , id);
        log.info("执行数据库减库存：num-->{}" , num);
        int i = seckillDao.reduceStock(id, num);
        log.info("执行数据库减库存：影响行数-->{}" , i);
    }
}
