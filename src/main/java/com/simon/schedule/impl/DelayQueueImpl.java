package com.simon.schedule.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.simon.entity.DelayData;
import com.simon.schedule.DelayQueue;
import com.simon.service.CacheService;
import com.simon.util.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @data: 2020/7/31 16:06
 * @author: limeng17
 * @version:
 * @description: 延迟队列实现
 */
@Component
public class DelayQueueImpl implements DelayQueue {

    @Autowired
    private CacheService cacheService;

    /**
     * 缓存数据Key前缀
     */
    private String keyPrefix = "resis_delay_queue_";

    /**
     * 扫描间隔 1秒
     */
    private long interval = 1000;

    /**
     * 延迟队列实体执行调用的线程池
     */
    private ExecutorService executors = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),new DefaultThreadFactory("delay_queue_thread"));

    private CountDownLatch countDownLatch = null;

    /**
     * 获取延迟队列中的数据并执行
     * @throws Exception
     */
    @Override
    @PostConstruct
    public void start() throws Exception {
        new Thread(() -> {
            // 当前时间
            Long currentTime = 0L;
            while (true){
                // 任务执行开始时间
                Long startTime = System.currentTimeMillis();
                try {
                    // 小于扫描间隔，程序等待再执行
                    if(currentTime < getInterval()){
                        Thread.sleep(getInterval() - currentTime);
                    }
                    // 获取已到执行时间的任务
                    Set<String> delayDataIdSet = cacheService.zrangeByScore(getSetKey(), 0, System.currentTimeMillis());
                    if(CollectionUtil.isEmpty(delayDataIdSet)){
                        continue;
                    }

                    // 执行任务
                    countDownLatch = new CountDownLatch(delayDataIdSet.size());
                    for (String delayDataId : delayDataIdSet) {
                        try {
                            executors.submit(new Task(delayDataId,getSetKey(),getDataKey(delayDataId)));
                        }catch (Exception e){
                            countDownLatch.countDown();
                        }
                    }
                    countDownLatch.await();
                }catch (Exception e){

                }finally {
                    currentTime = System.currentTimeMillis() - startTime;
                }
            }
        }).start();
    }

    @Override
    public void push(DelayData data) throws Exception {
        // 暂存数据ID到zset
        String delayDataId = UUID.randomUUID().toString();
        data.setExecuteTimeSlotIndex(0);
        cacheService.zadd(getSetKey(),delayDataId,System.currentTimeMillis() + data.delay() * 1000L);
        // 暂存数据
        cacheService.set(getDataKey(delayDataId), JSON.toJSONString(data),data.expired());
    }

    /**
     * 任务实体执行类
     */
    public class Task implements Runnable{

        private String delayDataId;
        private String setKey;
        private String dataKey;

        public Task(String delayDataId, String setKey, String dataKey) {
            this.delayDataId = delayDataId;
            this.setKey = setKey;
            this.dataKey = dataKey;
        }

        @Override
        public void run() {

            String lockKey = getLockKey(delayDataId);
            try {
                // 分布式锁,防止并发处理任务 TODO 这分布式锁有问题
                if(cacheService.lock(lockKey,10)){
                    String delayData = cacheService.get(dataKey);
                    if(StrUtil.isEmpty(delayData)){
                        // 缓存数据为空，删除保存在zset中的数据ID
                        cacheService.zrem(setKey,delayDataId);
                        return;
                    }

                    // 获取任务
                    DelayData delay = JSON.parseObject(delayData, DelayData.class);
                    try {
                        // 真正执行任务
                        delay.doExecute();
                        // 删除记录的任务
                        cacheService.zrem(setKey, delayDataId);
                        cacheService.del(dataKey);
                    }catch (Exception e){
                        // 执行失败,没有机会
                        if(delay.getNextExecuteTimeSlotIndex() >= delay.getDelaySeconds().length){
                            // TODO 执行失败，记录日志
                            return;
                        }

                        // 执行失败,有机会,再次放入队列
                        cacheService.zadd(setKey,delayDataId,System.currentTimeMillis() + delay.delay() * 1000L);
                        cacheService.set(dataKey, delayData,delay.expired());

                    }
                }
            }finally {
                countDownLatch.countDown();
            }
        }
    }

    public String getSetKey() {
        return this.keyPrefix + "set";
    }

    public String getDataKey(String delayDataId) {
        return this.keyPrefix + delayDataId;
    }

    public String getLockKey(String delayDataId){
        return delayDataId + "_lock";
    }

    public long getInterval() {
        return interval;
    }
}
