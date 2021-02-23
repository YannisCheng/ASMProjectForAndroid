package com.cwj.myapplication;

import com.cwj.myapplication.sdk.IgnoreTraceMethodCostClass;

/**
 * @author wenjia.Cheng  cwj1714@163.com
 * @date 2021/2/20
 */
@IgnoreTraceMethodCostClass
public class TargetClassIgnoreClass {

    public void showMethod(){
        System.out.println("method");
    }
}
