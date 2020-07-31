package com.simon.controller;

import com.simon.entity.DelayData;
import com.simon.schedule.DelayQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @data: 2020/7/31 18:01
 * @author: limeng17
 * @version:
 * @description:
 */
@RestController
@RequestMapping("/delay/queue")
public class DelayQueueController {

    @Autowired
    private DelayQueue delayQueue;

    @RequestMapping("execute")
    public void execute(Integer base){
        try {
            delayQueue.push(DelayData.init("delayQueueService","calDouble",base).initDelay(10,20,30));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
