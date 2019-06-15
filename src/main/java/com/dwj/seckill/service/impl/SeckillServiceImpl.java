package com.dwj.seckill.service.impl;

import com.dwj.seckill.common.BaseResponse;
import com.dwj.seckill.dao.SeckillDao;
import com.dwj.seckill.dao.entity.SeckillItem;
import com.dwj.seckill.enums.SeckillState;
import com.dwj.seckill.model.SeckillItemVo;
import com.dwj.seckill.model.SeckillResult;
import com.dwj.seckill.model.SeckillUrlResult;
import com.dwj.seckill.service.SeckillService;
import com.dwj.seckill.service.SeckillTaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.*;

import static com.dwj.seckill.constant.RedsKey.Seckill.*;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillTaskService seckillTaskService;

    private static final String salt = "@&*$HB/*6FE39";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SeckillItemVo> list() {
        List<SeckillItemVo> vos = new ArrayList<>();
        Map<String, SeckillItem> map = redisTemplate.opsForHash().entries(SECKILL_ITEM);
        if (null != map && map.size() > 0) {
            Collection<SeckillItem> list = map.values();
            for (SeckillItem seckillItem : list) {
                SeckillItemVo vo = new SeckillItemVo();
                BeanUtils.copyProperties(seckillItem, vo);
                //TODO 获取商品秒杀状态
                SeckillState seckillState = seckillState(seckillItem);
                vo.setSeckillState(seckillState.getCode());
                vo.setNow(new Date());
                switch (seckillState) {
                    case SECKILLING:
                        vo.setMd5(getMd5(seckillItem.getId()));
                        break;
                }
                //TODO 获取最新库存
                Long size = redisTemplate.opsForList().size(SECKILL_STOCK + seckillItem.getId());
                vo.setStock(size.intValue());
                vos.add(vo);
            }
            //排序
            Collections.sort(vos, (o1, o2) -> (int)(o1.getId() - o2.getId()));
        }
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> state(Boolean isOpen) {
        if (isOpen) {
            List<SeckillItem> itemList = seckillDao.list();
            if (null == itemList || itemList.size() <= 0) {
                return BaseResponse.error("seckill goods size is zero!");
            }
            //清除旧商品缓存
            Set keys = redisTemplate.keys("seckill:*");
            for (Object key : keys) {
                redisTemplate.delete(key);
            }
            //缓存商品、库存
            itemList.forEach(v -> {
                //缓存商品
                redisTemplate.opsForHash().put(SECKILL_ITEM, v.getId(), v);
                //缓存库存
                Integer stock = v.getStock();
                List<Integer> stocks = new ArrayList<>();
                for (Integer i = 0; i < stock; i++) {
                    stocks.add(i);
                }
                if(stocks.size() > 0){
                    redisTemplate.opsForList().leftPushAll(SECKILL_STOCK + v.getId(), stocks);
                }
                //TODO 开启定时同步库存到数据库
                seckillTaskService.syncStock(v.getId());
            });
            return BaseResponse.success("seckill isOpen!");
        } else {
            List<SeckillItem> itemList = seckillDao.list();
            //TODO 关闭库存同步任务
            itemList.forEach(v -> seckillTaskService.syncStockShutDown(v.getId()));
            Set keys = redisTemplate.keys("seckill:*");
            for (Object key : keys) {
                redisTemplate.delete(key);
            }
            return BaseResponse.success("seckill closed!");
        }
    }

    /**
     * 秒杀接口: 返回success表示校验通过,SeckillResult对象才代表最终秒杀结果
     * @param id
     * @param md5
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<SeckillResult> seckill(Long id, String md5) {
        if(!md5.equals(getMd5(id))){
            return BaseResponse.error("Please try again later!");
        }
        Object itemObj = redisTemplate.opsForHash().get(SECKILL_ITEM, id);
        if(itemObj != null){
            SeckillItem seckillItem = (SeckillItem) itemObj;
            SeckillState state = seckillState(seckillItem);
            SeckillResult res = new SeckillResult();
            switch (state) {
                case NU_SECKILL:
                    Long originalStock = redisTemplate.opsForList().size(SECKILL_STOCK + id);
                    res.setId(id);
                    res.setMsg("Seckill Not Opened!");
                    res.setResult(0);
                    res.setNow(new Date());
                    res.setStartDate(seckillItem.getStartDate());
                    res.setEndDate(seckillItem.getEndDate());
                    res.setState(state.getCode());
                    res.setStock(originalStock.intValue());
                    return BaseResponse.success(res);
                case SECKILLING:
                    boolean b = reduceStock(id);
                    Long seckillStock = redisTemplate.opsForList().size(SECKILL_STOCK + id);
                    if(b){
                        //获取最新库存量
                        res.setId(id);
                        res.setMsg("Success!！！！");
                        res.setResult(1);
                        res.setNow(new Date());
                        res.setStartDate(seckillItem.getStartDate());
                        res.setEndDate(seckillItem.getEndDate());
                        res.setState(state.getCode());
                        res.setStock(seckillStock.intValue());
                        return BaseResponse.success(res);
                    }else {
                        res.setId(id);
                        res.setMsg("Insufficient stock!");
                        res.setResult(0);
                        res.setNow(new Date());
                        res.setStartDate(seckillItem.getStartDate());
                        res.setEndDate(seckillItem.getEndDate());
                        res.setState(state.getCode());
                        res.setStock(seckillStock.intValue());
                        return BaseResponse.success(res);
                    }
                case SECKILL_END:
                    Long endStock = redisTemplate.opsForList().size(SECKILL_STOCK + id);
                    res.setId(id);
                    res.setMsg("Seckill is over!");
                    res.setResult(0);
                    res.setNow(new Date());
                    res.setStartDate(seckillItem.getStartDate());
                    res.setEndDate(seckillItem.getEndDate());
                    res.setState(state.getCode());
                    res.setStock(endStock.intValue());
                    return BaseResponse.success(res);
            }
        }else {
            return BaseResponse.error("Goods are off the shelf!");
        }

        return BaseResponse.error("Unknown!");
    }

    /**
     * 获取秒杀地址接口: 返回success表示校验通过,SeckillUrlResult对象才代表最终获取地址结果
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<SeckillUrlResult> url(Long id) {
        Object itemObj = redisTemplate.opsForHash().get(SECKILL_ITEM, id);
        if(itemObj != null){
            SeckillUrlResult res = new SeckillUrlResult();
            SeckillItem seckillItem = (SeckillItem) itemObj;
            SeckillState state = seckillState(seckillItem);
            switch (state) {
                case NU_SECKILL://未开始
                    res.setId(id);
                    res.setMsg("Seckill Not Opened!");
                    res.setResult(0);
                    res.setNow(new Date());
                    res.setStartDate(seckillItem.getStartDate());
                    res.setEndDate(seckillItem.getEndDate());
                    res.setState(state.getCode());
                    return BaseResponse.success(res);
                case SECKILLING://开始
                    res.setId(id);
                    res.setMsg("Success!");
                    res.setResult(1);
                    res.setNow(new Date());
                    res.setMd5(getMd5(id));
                    res.setStartDate(seckillItem.getStartDate());
                    res.setEndDate(seckillItem.getEndDate());
                    res.setState(state.getCode());
                    return BaseResponse.success(res);
                case SECKILL_END://结束
                    res.setId(id);
                    res.setMsg("Seckill is over!");
                    res.setResult(0);
                    res.setNow(new Date());
                    res.setStartDate(seckillItem.getStartDate());
                    res.setEndDate(seckillItem.getEndDate());
                    res.setState(state.getCode());
                    return BaseResponse.success(res);
            }
        }else {
            return BaseResponse.error("Goods are off the shelf!");
        }
        return BaseResponse.error("Unknown!");
    }

    /**
     * 处理减库存
     *
     * @param id
     * @return
     */
    private boolean reduceStock(Long id) {
        Object obj = redisTemplate.opsForList().leftPop(SECKILL_STOCK + id);
        if(obj != null){
            //TODO 异步减库存 推荐定时更新数据库
            //TODO 异步添加购买明细 推荐定时更新数据库
            //seckillDao.reduceStock(id,1); // 同步，不推荐
            //seckillTaskService.reduceStock(id,1); // 异步，高并发仍不推荐
            return true;
        }
        return false;
    }

    /**
     * 获取商品秒杀状态
     *
     * @param seckillItem
     * @return
     */
    private SeckillState seckillState(SeckillItem seckillItem) {
        long now = new Date().getTime();
        long startTime = seckillItem.getStartDate().getTime();
        long endTime = seckillItem.getEndDate().getTime();
        if (startTime <= now && now <= endTime) {
            return SeckillState.SECKILLING;
        }
        if (now > endTime) {
            return SeckillState.SECKILL_END;
        }
        return SeckillState.NU_SECKILL;
    }

    /**
     * 获取md5
     * @param id
     * @return
     */
    private String getMd5(Long id){
        if(null != id){
            return DigestUtils.md5DigestAsHex((id + salt).getBytes());
        }
        return null;
    }
}
