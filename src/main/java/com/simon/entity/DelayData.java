package com.simon.entity;

import cn.hutool.core.util.ObjectUtil;
import com.simon.util.ApplicationContextUtil;
import com.simon.util.LocalInvoker;

import java.io.Serializable;

/**
 * @data: 2020/7/31 16:06
 * @author: limeng17
 * @version:
 * @description: 任务
 */
public class DelayData implements Serializable {

    private static final long serialVersionUID = 5339636145978889346L;
    private String beanName;
    private String beanMethod;
    private Object[] beanArgs;

    /**
     * 延迟执行时间数组
     */
    private int[] delaySeconds;
    /**
     * 当前延迟执行时间数组下标
     */
    private int executeTimeSlotIndex = 0;

    public static DelayData init(String beanName, String beanMethod, Object... beanArgs) {
        DelayData delayData = new DelayData();
        delayData.setBeanName(beanName);
        delayData.setBeanMethod(beanMethod);
        delayData.setBeanArgs(beanArgs);
        return delayData;
    }

    /**
     * 获取延迟执行时间
     * @return
     */
    public int delay() {
        if (this.delaySeconds == null || this.executeTimeSlotIndex >= this.delaySeconds.length) {
            return 10;
        }
        int value = this.delaySeconds[this.executeTimeSlotIndex];
        if (value < 0) {
            value = 0;
        }
        return value;
    }

    /**
     * 初始化delaySeconds
     * @param delaySeconds
     * @return
     */
    public DelayData initDelay(int... delaySeconds){
        this.setDelaySeconds(delaySeconds);
        return this;
    }

    /**
     * 数据默认过期时间
     * @return
     */
    public int expired() {
        return 3600 * 24 + this.delay();
    }

    /**
     * 获取下一个延迟执行时间槽
     * @return
     */
    public int getNextExecuteTimeSlotIndex() {
        return ++executeTimeSlotIndex;
    }

    /**
     * 执行任务
     * @return
     */
    public Object doExecute() throws Exception {

        Object bean = ApplicationContextUtil.getBean(beanName);
        if(ObjectUtil.isNull(bean)){
            return null;
        }

        return new LocalInvoker(bean,beanMethod,beanArgs).invoke();
    };


    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanMethod() {
        return beanMethod;
    }

    public void setBeanMethod(String beanMethod) {
        this.beanMethod = beanMethod;
    }

    public Object[] getBeanArgs() {
        return beanArgs;
    }

    public void setBeanArgs(Object[] beanArgs) {
        this.beanArgs = beanArgs;
    }

    public int[] getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int[] delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public int getExecuteTimeSlotIndex() {
        return executeTimeSlotIndex;
    }

    public void setExecuteTimeSlotIndex(int executeTimeSlotIndex) {
        this.executeTimeSlotIndex = executeTimeSlotIndex;
    }
}
