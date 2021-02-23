package com.cwj.myapplication

import android.app.Application
import android.content.Context

/**
 *
 * @author  wenjia.Cheng  cwj1714@163.com
 * @date    2021/2/22
 */
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Thread.sleep(300)
    }

    override fun onCreate() {
        super.onCreate()
        Thread.sleep(300)
    }
}