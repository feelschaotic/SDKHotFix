package com.feelschaotic.sdkhotfix.sdk.statistics

import com.feelschaotic.sdkhotfix.sdk.HotfixManager

/**
 * 统计管理器，用于划分埋点和使用方的边界
 * @author feelschaotic
 * @create 2019/6/6.
 */
object StatisticsManager {

    fun track(scene: Int, extras: String? = null, extraCode: String? = null, extraDesc: String? = null) {
        HotfixManager.config.listener?.let {
            val map = HashMap<String, Any>()
            map[StatisticsConstants.EVENT_SCENE] = scene.toString()
            if (!extras.isNullOrEmpty()) {
                map[StatisticsConstants.EVENT_EXTRAS] = extras!!
            }
            if (!extraCode.isNullOrEmpty()) {
                map[StatisticsConstants.EXTRA_CODE] = extraCode!!
            }

            if (!extraDesc.isNullOrEmpty()) {
                map[StatisticsConstants.EXTRA_DESC] = extraDesc!!
            }
            it.onTrack(StatisticsConstants.EVENT_KEY, map)
        }
    }
}