package com.cwj.sdklib.method_stack
import org.json.JSONObject

/**
 *
 * @author  wenjia.Cheng  cwj1714@163.com
 * @date    2021/1/12
 */
class AppTimeCounterManager private constructor() {
    val mAppCounter = AppCounter()

    companion object {
        val appTimeCounterManager: AppTimeCounterManager by lazy {
            AppTimeCounterManager()
        }
    }

    /**
     * App attachBaseContext
     */
    fun onAppAttachBaseContextStart() {
        mAppCounter.attachStart();
    }

    /**
     * App attachBaseContext
     */
    fun onAppAttachBaseContextEnd() {
        mAppCounter.attachEnd()
    }

    /**
     * App 启动
     */
    fun onAppCreateStart() {
        mAppCounter.start()
    }

    /**
     * App 启动结束
     */
    fun onAppCreateEnd() {
        mAppCounter.end()
        trackApplicationMethodCost()
    }

    private fun trackApplicationMethodCost() {
        mAppCounter.appSetupInfo
        val methodProperties = JSONObject()
        methodProperties.put("total_cost", mAppCounter.appSetupInfo.totalCost)
        methodProperties.put(
            "attach_base_context_cost",
            mAppCounter.appSetupInfo.attachBaseContextCost
        )
        methodProperties.put("create_cost", mAppCounter.appSetupInfo.createCost)
        println("methodProperties is : $methodProperties")
    }
}