package com.feelschaotic.samplesdk

import android.app.Application
import android.util.Log
import com.meituan.robust.patch.annotaion.Modify
import java.lang.ref.WeakReference
import java.util.*

/**
 * 我是一个业务方示例sdk，我接入了hotfix库
 * @author feelschaotic
 * @create 2019/9/30.
 */
object SdkManager {
    private var applicationRef: WeakReference<Application>? = null

    fun init(application: Application) {
        applicationRef = WeakReference(application)

    }

    fun fixBug() {
        Log.d("TAG", "初始化热修复sdk")

        applicationRef?.get()?.let {
            val hotfixConfig = HotfixConfig.Builder()
                .debug(BuildConfig.DEBUG)//为true开启内部log打印
                .packageName(BuildConfig.APPLICATION_ID + "_" + BuildConfig.SDK_ARTIFACT_ID)//视情况加上ARTIFACT_ID 有时候会出现业务方sdk需要提供多ARTIFACT_ID的情况
                .appVersion(BuildConfig.SDK_VERSION)
                .statisticsListener(object : StatisticsListener {
                    override fun onTrack(eventName: String, eventParams: HashMap<String, Any>?) {
                        Log.d("TAG", "热更埋点触发 $eventName $eventParams")
                    }
                })
                .build()
            HotfixManager.init(it, hotfixConfig)
        }
    }

    fun callBug() {
            var i = 1 / 0
    }
}