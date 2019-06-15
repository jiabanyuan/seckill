package com.dwj.seckill.service.impl;

import com.dwj.seckill.job.ReduceStockJob;
import com.dwj.seckill.job.SyncStockJob;
import com.dwj.seckill.manager.BaseJobManager;
import com.dwj.seckill.service.SeckillTaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
public class SeckillTaskServiceImpl implements SeckillTaskService {

    @Autowired
    private BaseJobManager baseJobManager;

    @Override
    public boolean reduceStock(Long id, int num) {
        try {
            HashMap<String, Object> param = new HashMap<>();
            param.put("id", id);
            param.put("num", num);
            baseJobManager.createOnceSimpleJob("SECKILL_REDUCE_STOCK_JOB" + UUID.randomUUID().toString(), ReduceStockJob.class, new Date(), param);
            return true;
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean syncStock(Long id) {
        try {
            HashMap<String, Object> param = new HashMap<>();
            param.put("id", id);
            baseJobManager.createCronJob("SYNC_STOCK_JOB" + id, SyncStockJob.class, "0 */1 * * * ?", param);
            return true;
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean syncStockShutDown(Long id) {
        try {
            baseJobManager.removeDefaultJob("SYNC_STOCK_JOB" + id);
            return true;
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
