package com.simon.controller;

import com.simon.annotation.Cache;
import com.simon.annotation.ClearCache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @data: 2020/6/15 18:17
 * @author: limeng17
 * @version:
 * @description:
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

    @Cache(expireKey = "cache.select.by.stu.id",featureCode = "stu.cache",defaultExpired = 120)
    @RequestMapping(value = "selectByStuId",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public String selectByStuId(@RequestParam(value = "stu_id") String stu_id){
        return stu_id;
    }

    @Cache(expireKey = "cache.select.by.stu.name",featureCode = "stu.cache",defaultExpired = 120)
    @RequestMapping(value = "selectByStuName",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public String selectByStuName(@RequestParam(value = "stu_name") String stu_name){
        return stu_name;
    }

    @ClearCache(featureCode = "stu.cache")
    @RequestMapping(value = "batchUpdate",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public String batchUpdate(){
        return "batch.update";
    }
}
