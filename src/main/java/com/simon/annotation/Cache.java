package com.simon.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 缓存注解
 * @Author limeng17
 * @Date 2020/6/16 11:08
 * @Version 1.0
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface Cache {

    /**
     * 过期时间默认key
     * @return
     */
    String expireKey() default "cache.expire.key.default";

    /**
     * 默认过去时间 秒
     * @return
     */
    int defaultExpired() default 60;


    /**
     * 特征码，针对相同特征的接口
     * @return
     */
    String featureCode() default "feature.code.key.default";

}
