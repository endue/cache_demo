package com.simon.aop;

import com.simon.annotation.ClearCache;
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
 * @data: 2020/6/16 18:35
 * @author: limeng17
 * @version:
 * @description:
 */
@Component
@Aspect
public class ClearCacheHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClearCacheHandler.class);

    private String useCacheStr = "true";

    /**
     * uri缓存过期配置
     */
    private static final Map<String, ClearCache> URI_EXPIRE_MAP = new ConcurrentHashMap();

    @Autowired
    private CacheService cacheService;

    @Around(value = "execution(* com.simon..*.*(..))&&@annotation(com.simon.annotation.ClearCache)")
    private Object cacheAround(ProceedingJoinPoint joinPoint) throws Throwable {

        boolean error = false;
        boolean useCache = Boolean.valueOf(useCacheStr);

        ClearCache clearCache = useCache ? getAnnotation(joinPoint,getUri()) : null;

        try {
            return joinPoint.proceed();
        }catch (Exception e){
            error = true;
            throw e;
        }finally {
            /**
             * 首先缓存注解不为空
             *  一、异常后更新版本号并发生了异常
             *  二、异常后不更新版本号
             */
            if(clearCache != null && (!clearCache.exceptionIncr() || (error && clearCache.exceptionIncr()))){
                incrFeatureCode(clearCache);
            }
        }
    }

    private void incrFeatureCode(ClearCache clearCache) {
        if (clearCache == null || clearCache.featureCode() == null || clearCache.featureCode().length < 1) {
            return;
        }
        try {
            for (String code : clearCache.featureCode()) {
                cacheService.incr(code);
            }
        }catch (Exception e){

        }
    }


    private ClearCache getAnnotation(JoinPoint point, String uri) {
        if (uri == null || uri.length() == 0) {
            return null;
        }
        try {
            if (URI_EXPIRE_MAP.containsKey(uri)) {
                return URI_EXPIRE_MAP.get(uri);
            }
            Method method = getMethod(point);
            if (method != null && method.isAnnotationPresent(ClearCache.class)) {
                URI_EXPIRE_MAP.put(uri, method.getAnnotation(ClearCache.class));
            }
            return URI_EXPIRE_MAP.get(uri);
        } catch (Exception err) {
            logger.error("getAnnotation err", err);
            return null;
        }
    }


    private String getUri() {
        try {
            return UriUtils.getUri(SysContent.getRequest());
        } catch (Exception err) {
            logger.error("getUri err", err);
            return null;
        }
    }

    private Method getMethod(JoinPoint point) {
        try {
            Method target = ((MethodSignature) point.getSignature()).getMethod();
            return point.getTarget().getClass().getMethod(target.getName(), target.getParameterTypes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
