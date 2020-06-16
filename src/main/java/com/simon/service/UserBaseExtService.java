package com.simon.service;

import com.simon.annotation.Cache;
import org.springframework.stereotype.Service;

/**
 * @data: 2020/6/10 18:13
 * @author: simon
 * @version:
 * @description:
 */
@Service
public class UserBaseExtService {

    @Cache(expireKey = "cache.select.by.stu.name",featureCode = "stu.cache",defaultExpired = 120)
    public String selectByStuId(String stu_id){
        return stu_id;
    }
}
