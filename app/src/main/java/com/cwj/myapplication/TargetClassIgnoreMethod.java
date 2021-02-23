package com.cwj.myapplication;


import com.cwj.sdklib.IgnoreTraceMethodCostMethod;

/**
 * @author wenjia.Cheng  cwj1714@163.com
 * @date 2021/2/20
 */
public class TargetClassIgnoreMethod {

    public void showMethod1(){
        System.out.println("method1");
    }

    @IgnoreTraceMethodCostMethod
    public void showMethod2(){
        System.out.println("method2");
    }

}
