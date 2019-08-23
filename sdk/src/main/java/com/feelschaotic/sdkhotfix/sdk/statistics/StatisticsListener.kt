package com.feelschaotic.sdkhotfix.sdk.statistics

/**
 * @author feelschaotic
 * @create 2019/8/6.
 */
interface StatisticsListener {
    fun onTrack(eventName: String, eventParams: HashMap<String, Any>?)
}