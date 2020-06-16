package com.simon.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 清空缓存注解
 * @Author limeng17
 * @Date 2020/6/16 18:24
 * @Version 1.0
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface ClearCache {

    /**
     * 特征码，针对相同特征的接口
     * @return
     */
    String[] featureCode() default "feature.code.key.default";

    /**
     * 当异常时是否更新版本号，默认为false，即不更新
     * @return
     */
    boolean exceptionIncr() default false;
}
