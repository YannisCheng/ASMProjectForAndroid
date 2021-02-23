package com.cwj.myapplication.sdk;


/**
 * ComputeTargetCost  提供计算方法耗时开始、结束方法的类
 *
 * @author  wenjia.Cheng  cwj1714@163.com
 * @date    2021/2/5 14:17
*/
public class ComputeTargetCost {

    private static final ComputeCostRealValue costCompute = new ComputeCostRealValue();

    public static void startTime(String keyStr){
        costCompute.startValue(keyStr);
    }
    // com.sensorsdata.analytics.android.demo.activity.TestMainAdapter$onBindViewHolder$1&onClick
    public static void stopTime(String keyStr){
        costCompute.endValue(keyStr);
    }
}

