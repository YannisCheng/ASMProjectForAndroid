package com.cwj.myapplication.sdk;

/**
 * ComputeCostRealValue  具体记录方法开始、结束时间点的类
 *
 * @author wenjia.Cheng  cwj1714@163.com
 * @date 2021/2/5 14:18
 */
public class ComputeCostRealValue {

    private long startTime = 0L;

    public void startValue(String keyStr) {
        startTime = System.currentTimeMillis();
        System.out.printf("key is : %s, statTime is %s: %n", keyStr, startTime);
    }

    public void endValue(String keyStr) {
        long endTime = System.currentTimeMillis();
        System.out.printf("end time is : %s %n",endTime);
        System.out.printf("key is : %s, endTime is :%s %n", keyStr, (endTime - startTime));
    }
}
