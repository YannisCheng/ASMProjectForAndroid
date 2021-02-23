package com.cwj.sdklib;

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.cwj.sdklib.method_stack.AppTimeCounterManager
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap


object MethodCostUtil {
    private const val TAG = "DOKIT_SLOW_METHOD"

    /**
     * key className&method
     */
    private val METHOD_COSTS: ConcurrentHashMap<String, Long?> by lazy { ConcurrentHashMap<String, Long?>() }

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun recodeStaticMethodCostStart(methodName: String) {
        //Log.e(TAG, "recodeStaticMethodCostStart methodName is : $methodName")
        METHOD_COSTS[methodName] = System.currentTimeMillis()
    }

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun recodeStaticApplicationMethodCostStart(methodName: String) {
        //Log.e(TAG, "recodeStaticMethodCostStart methodName is : $methodName")
        METHOD_COSTS[methodName] = System.currentTimeMillis()
        val methods = methodName.split("&".toRegex()).toTypedArray()
        if (methods.size == 2) {
            if (methods[1] == "onCreate") {
                AppTimeCounterManager.appTimeCounterManager.onAppCreateStart()
            }
            if (methods[1] == "attachBaseContext") {
                AppTimeCounterManager.appTimeCounterManager.onAppAttachBaseContextStart()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun recodeStaticMethodCostEnd(methodAbout: String) {
        //Log.e(TAG, "recodeObjectMethodCostEnd: $methodAbout")
        var blockTime: Int = 0
        blockTime = 0
        synchronized(MethodCostUtil::class.java) {
            try {
                if (METHOD_COSTS.containsKey(methodAbout)) {
                    val startTime = METHOD_COSTS[methodAbout]!!
                    val costTime = (System.currentTimeMillis() - startTime).toInt()
                    METHOD_COSTS.remove(methodAbout)
                    //如果该方法的执行时间大于 thresholdTime 则记录
                    if (costTime >= blockTime) {
                        trackMethodCost(
                            methodAbout,
                            Thread.currentThread(),
                            blockTime,
                            costTime
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun recodeStaticApplicationMethodCostEnd(methodAbout: String) {
        //Log.e(TAG, "ApplicationMethodCostEnd: $methodAbout")
        synchronized(MethodCostUtil::class.java) {
            try {
                if (METHOD_COSTS.containsKey(methodAbout)) {
                    METHOD_COSTS.remove(methodAbout)
                    val methods = methodAbout.split("&".toRegex()).toTypedArray()
                    if (methods.size == 2) {
                        if (methods[1] == "onCreate") {
                            AppTimeCounterManager.appTimeCounterManager.onAppCreateEnd()
                            return
                        }
                        if (methods[1] == "attachBaseContext") {
                            AppTimeCounterManager.appTimeCounterManager.onAppAttachBaseContextEnd()
                            return
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 非Application类的方法耗时数据构造
     */
    private fun trackMethodCost(
        methodAbout: String,
        thread: Thread,
        thresholdTime: Int,
        costTime: Int
    ) {
        val methods = methodAbout.split("&".toRegex()).toTypedArray()
        val stringBuilder = StringBuilder()
        val methodProperties = JSONObject()

        val stackTraceElements = thread.stackTrace
        /*stackTraceElements.forEach {
            Log.e(TAG, "stackTraceElements item is : $it")
        }*/
        val replace = methods[0].replace("/", ".") + "." + methods[1];
        for (stackTraceElement in stackTraceElements) {
            if (stackTraceElement.toString().contains("MethodCostUtil")) {
                continue
            }
            if (stackTraceElement.toString()
                    .contains("dalvik.system.VMStack.getThreadStackTrace")
            ) {
                continue
            }
            if (stackTraceElement.toString().contains("java.lang.Thread.getStackTrace")) {
                continue
            }
            if (stackTraceElement.toString().contains("ProxyHandlerCallback")) {
                continue
            }
            if (stackTraceElement.toString().contains(replace)) {
                stringBuilder.append("${stackTraceElement}")
            }
        }
        methodProperties.put("method_position", stringBuilder.toString())
        // 方法所在类
        // methodProperties.put("method_class", methods[0]);
        // 方法名
        methodProperties.put("method_name", methods[1]);
        // 耗时阈值
        methodProperties.put("threshold_time", thresholdTime);
        // 此方法实际耗时
        methodProperties.put("cost_time", costTime);
        // 方法所在线程
        methodProperties.put("method_thread", thread.name);
        println("methodProperties is :$methodProperties")
    }


}