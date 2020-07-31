package com.simon.service;

import org.springframework.stereotype.Service;

/**
 * @data: 2020/7/31 18:02
 * @author: limeng17
 * @version:
 * @description:
 */
@Service("delayQueueService")
public class DelayQueueService {

    public void calDouble(Integer base){
        System.out.println(System.currentTimeMillis() + "===" + base);
    }
}
