package com.feelschaotic.sdkhotfix.sdk.service

/**
 * service接口请求结果回调
 * @author feelschaotic
 * @create 2019/5/5.
 */

interface RespondListener<in T> {
    fun onSuccess(response: T)
    fun onError(e: Exception)
}
