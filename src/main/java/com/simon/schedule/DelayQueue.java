package com.simon.schedule;

import com.simon.entity.DelayData;

/**
 * @data: 2020/7/31 16:05
 * @author: limeng17
 * @version:
 * @description: 延迟队列
 */
public interface DelayQueue {

    /**
     * 启动延迟队列
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 放入数据到延迟队列
     * @param data
     * @throws Exception
     */
    void push(DelayData data) throws Exception;
}
