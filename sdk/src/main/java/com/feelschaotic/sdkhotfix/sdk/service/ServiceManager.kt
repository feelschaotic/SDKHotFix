package com.feelschaotic.sdkhotfix.sdk.service

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.feelschaotic.sdkhotfix.sdk.BuildConfig
import com.feelschaotic.sdkhotfix.sdk.HotfixManager
import com.feelschaotic.sdkhotfix.sdk.entity.CheckVersionRespData
import com.feelschaotic.sdkhotfix.sdk.entity.DownloadRequest
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsConstants
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsManager
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils

/**
 * service管理器，用于包装网络层PatchService，做除了网络请求之外的处理
 * @author feelschaotic
 * @create 2019/6/5.
 */
object ServiceManager {

    private var patchService: PatchService = PatchService()
    private var TAG = "sdk-patch"
    private val CODE_SUCCESS = 0

    private val PACKAGE_NAME = "packagename"
    private val BUNDLE_VERSION = "bundleversion"
    private val PLATFORM = "platform"
    private val APP_VERSION = "appversion"

    fun checkVersion(onCheckVersionListener: ((CheckVersionRespData) -> Unit?)?) {
        val map = mutableMapOf<String, String>()
        map.put(PACKAGE_NAME, HotfixManager.config.packageName + "_" + HotfixManager.config.appVersion)
        map.put(BUNDLE_VERSION, "0")
        map.put(PLATFORM, "android")
        map.put(APP_VERSION, HotfixManager.config.appVersion!!)

        patchService.checkVersion(BuildConfig.HOTFIX_SERVER_URL + "/checkversion", map, object : RespondListener<String> {
            override fun onSuccess(response: String) {
                try {
                    LogUtils.d(TAG, "response:$response")
                    disposeResp(response)
                } catch (e: JSONException) {
                    LogUtils.printError(e)
                } catch (e: ClassCastException) {
                    LogUtils.printError(e)
                }
            }

            private fun disposeResp(response: String) {
                val jsonObj: JSONObject = JSON.parse(response) as JSONObject

                if (CODE_SUCCESS != jsonObj.getIntValue("code")) {
                    LogUtils.d(TAG, "没有补丁信息")
                    return
                }

                val dataArray = jsonObj.getJSONArray("data")
                if (dataArray == null || dataArray.isEmpty()) {
                    return
                }
                val respData = JSONObject.toJavaObject(dataArray[0] as JSONObject, CheckVersionRespData::class.java)
                LogUtils.d(TAG, "有补丁，开始比较")
                StatisticsManager.track(StatisticsConstants.EventCode.CODE_REQUEST_NEW_PATCH, null, null, respData.bundleversion.toString())
                onCheckVersionListener?.invoke(respData)
            }

            override fun onError(e: Exception) {
                LogUtils.printError(e)
            }
        })
    }

    fun downloadPatch(bundleUrl: String, savePath: String, patchName: String, respondListener: RespondListener<String>) {
        val request = DownloadRequest()
        request.fileDir = savePath
        request.bucketName = BuildConfig.BUCK_NAME
        request.objectKey = bundleUrl
        request.patchName = patchName
        patchService.downloadPatch(request, respondListener)
    }
}
