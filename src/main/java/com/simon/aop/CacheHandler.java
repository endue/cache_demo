package com.simon.aop;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.simon.annotation.Cache;
import com.simon.service.CacheService;
import com.simon.util.SysContent;
import com.simon.util.UriUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @data: 2020/6/16 14:02
 * @author: limeng17
 * @version:
 * @description:
 */
@Component
@Aspect
public class CacheHandler {

    private static final Logger logger = LoggerFactory.getLogger(CacheHandler.class);

    @Autowired
    private CacheService cacheService;

    /**
     * 是否启用缓存
     */
    private String useCacheStr = "true";

    /**
     * 特征码过期时间，秒，默认5天
     */
    private Integer featureCodeExpired = 3600 * 24 * 5;

    /**
     * uri缓存过期配置
     */
    private static final Map<String, Cache> URI_EXPIRE_MAP = new ConcurrentHashMap<>();

    /**
     * uri缓存未配置集合
     */
    private static final Map<String, Integer> URI_EXPIRE_EMPTY_MAP = new ConcurrentHashMap<>();


    @Around(value = "execution(* com.simon..*.*(..)) && @annotation(com.simon.annotation.Cache)")
    private Object cacheAround(ProceedingJoinPoint joinPoint) throws Throwable {

        boolean useCache = Boolean.valueOf(useCacheStr);

        // 获取请求url
        String uri = useCache ? getUri() : null;
        // 获取缓存注解
        Cache cache = useCache ? this.getCache(joinPoint,uri) : null;
        // 获取注解过期时间
        int expired = useCache && cache != null ? getExpire(cache) : -1;;

        // 判断是否启用注解
        if (useCache && (cache == null || expired < 1)) {
            useCache = false;
        }
        // 获取方法传递的参数
        String args = useCache ? this.getArgs(joinPoint) : null;

        Object result = null;
        try {
            // 获取缓存值
            String cacheResult;
            if (useCache && uri != null && args != null && cache != null && (cacheResult = this.getCacheResult(joinPoint, cache, uri, args)) != null) {
                return cacheResult;
            }

           return (result = joinPoint.proceed());
        }finally {
            // result不为空并且开启缓存
            if (useCache && uri != null && args != null && cache != null && result != null && expired > 0) {
                this.cache(joinPoint,uri, args, cache, result, expired);
            }
        }
    }

    /**
     * 获取缓存结果
     *
     * @param joinPoint
     * @param cache
     * @param uri
     * @param args
     * @return
     */
    private String getCacheResult(ProceedingJoinPoint joinPoint, Cache cache, String uri, String args) {
        try{
            // 拼接缓存key
            String key = getKey(joinPoint,cache,uri,args);
            // 获取缓存结果
            return cacheService.get(key);
        }catch (Exception e){
            logger.error("error",e);
            return null;
        }
    }

    /**
     * 获取缓存key
     * @description 由 url + 参数 + 特征码版本号 构成。每个特征码有一个唯一的版本号(第一次生成时的时间戳)，如果让缓存失效时，将特征码版本号+1即可
     * @param joinPoint
     * @param cache
     * @param uri
     * @param args
     * @return
     */
    private String getKey(ProceedingJoinPoint joinPoint, Cache cache, String uri, String args) {
        try {
            // 格式化url和入参
            String key = (uri + "_" + args).replace("\"", "").replace("/", "_").replace(" ", "");
            return key + "_" + this.getSuffix(joinPoint,cache);
        }catch (Exception e){
            logger.error("error",e);
        }
        return "";
    }


    /**
     * 获取后缀
     *
     * @param joinPoint
     * @param cache
     * @return
     */
    private String getSuffix(ProceedingJoinPoint joinPoint, Cache cache) {
        if (cache == null || ObjectUtil.isEmpty(cache.featureCode())) {
            return "";
        }
        Long version = 0L;
        String keyValue = this.getKeyValue(joinPoint, cache.keyFieldIndex());
        try {
            String value = cacheService.get(cache.featureCode());
            if (value != null) {
                try {
                    version = Long.parseLong(value);
                } catch (Exception err) {
                    value = null;
                }
            }
            if (value == null) {
                version = System.currentTimeMillis();
                cacheService.set(cache.featureCode(), version + "", featureCodeExpired);
            }
        } catch (Exception err) {

        }
        return keyValue +"_"+ version.toString();
    }

    /**
     * 缓存结果
     * @param uri
     * @param args
     * @param cache
     * @param result
     * @param expired
     */
    private void cache(ProceedingJoinPoint joinPoint,String uri, String args, Cache cache, Object result, int expired) {
        this.cacheService.set(this.getKey(joinPoint, cache, uri, args), result.toString(), expired);
    }

    private String getKeyValue(ProceedingJoinPoint point, int keyFieldIndex) {
        try {
            if (keyFieldIndex < 0) {
                return null;
            }
            if (point.getArgs() == null || point.getArgs().length < keyFieldIndex) {
                return null;
            }
            return point.getArgs()[keyFieldIndex].toString();
        } catch (Exception err) {
            logger.error("", err);
        }
        return null;
    }

    /**
     * 获取参数值
     * @param joinPoint
     * @return
     */
    private String getArgs(ProceedingJoinPoint joinPoint) {
        try {
            if (joinPoint.getArgs() == null || joinPoint.getArgs().length == 0) {
                return "";
            }
            return JSON.toJSONString(joinPoint.getArgs());
        } catch (Exception err) {
            return null;
        }
    }

    /**
     * 获取过期时间
     * @param cache
     * @return
     */
    private int getExpire(Cache cache) {

        if(ObjectUtil.isNull(cache)){
            return -1;
        }

        int expire = 60;
        try {
            String value = null; // 从配置中心获取，配置中心key为：cache.expireKey()
            if (value == null || value.length() < 1) {
                return cache.defaultExpired();
            }
            return Integer.valueOf(value);
        }catch (Exception e){

        }

        return expire;
    }

    /**
     * 获取注解标签
     * @param point
     * @param uri
     * @return
     */
    private Cache getCache(JoinPoint point, String uri) {
        if (uri == null || uri.length() == 0) {
            return null;
        }
        try {
            if (URI_EXPIRE_MAP.containsKey(uri)) {
                return URI_EXPIRE_MAP.get(uri);
            }
            if (URI_EXPIRE_EMPTY_MAP.containsKey(uri)) {
                return null;
            }
            Method method = getMethod(point);
            if (method != null && method.isAnnotationPresent(Cache.class)) {
                URI_EXPIRE_MAP.put(uri, method.getAnnotation(Cache.class));
            } else {
                URI_EXPIRE_EMPTY_MAP.put(uri, 1);
            }
            return URI_EXPIRE_MAP.get(uri);
        } catch (Exception err) {
            return null;
        }
    }

    private Method getMethod(JoinPoint point) {
        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            return point.getTarget().getClass().getMethod(method.getName(),method.getParameterTypes());
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取url
     * @return
     */
    private String getUri() {
        try {
            return UriUtils.getUri(SysContent.getRequest());
        } catch (Exception err) {
            return null;
        }
    }
}
