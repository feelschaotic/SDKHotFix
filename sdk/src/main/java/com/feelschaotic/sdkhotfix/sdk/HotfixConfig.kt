package com.feelschaotic.sdkhotfix.sdk

import com.feelschaotic.sdkhotfix.sdk.exception.ConfigNotSetException
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsListener
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils

/**
 * @author feelschaotic
 * @create 2019/6/6.
 */
class HotfixConfig private constructor() {
    var debug: Boolean = false
    //业务sdk的包名
    var packageName: String? = null
    //业务sdk的版本
    var appVersion: String? = null
    //埋点回调
    var listener: StatisticsListener? = null

    class Builder {
        private var config: HotfixConfig = HotfixConfig()

        fun debug(debug: Boolean): Builder {
            config.debug = debug
            com.feelschaotic.sdkhotfix.sdk.utils.LogUtils.init(debug)
            return this
        }

        fun packageName(packageName: String): Builder {
            config.packageName = packageName
            return this
        }

        fun appVersion(appVersion: String): Builder {
            config.appVersion = appVersion
            return this
        }

        fun statisticsListener(listener: StatisticsListener): Builder {
            config.listener = listener
            return this
        }

        fun build(): HotfixConfig {
            return config
        }
    }

    fun checkConfig() {
        if (packageName == null) {
            throw ConfigNotSetException("packageName must be configured")
        }

        if (appVersion == null) {
            throw ConfigNotSetException("appVersion must be configured")
        }
    }
}