package com.feelschaotic.sdkhotfix.sdk.patch

import android.util.Log
import com.alibaba.fastjson.JSON
import com.feelschaotic.sdkhotfix.sdk.HotfixManager
import com.feelschaotic.sdkhotfix.sdk.const.Const
import com.feelschaotic.sdkhotfix.sdk.patch.PatchManipulateImp.Companion.TYPE_ONLY_LOAD_LOCAL
import com.feelschaotic.sdkhotfix.sdk.service.ServiceManager
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsConstants
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsManager
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils
import com.feelschaotic.sdkhotfix.sdk.utils.PPSharePreferenceHelper
import com.meituan.robust.Patch
import com.meituan.robust.PatchExecutor
import com.meituan.robust.RobustCallBack
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 补丁管理器
 * 用于加载本地补丁的和网络请求补丁
 * @author feelschaotic
 * @create 2019/6/5.
 */
object PatchManager {
    //  创建1个固定线程的线程池，用于串行进行本地补丁的加载和网络请求补丁然后加载的逻辑
    private var fixedThreadPool: ExecutorService = Executors.newFixedThreadPool(1)
    private val context = HotfixManager.getApplication()
    private const val TAG = "sdk-patch"
    private var localPatchInfo: com.feelschaotic.sdkhotfix.sdk.entity.CheckVersionRespData? = null

    /**
     * 加载补丁，进行补丁逻辑管理
     * 1. 如果有补丁则加载本地补丁
     * 2. 网络请求是否有新补丁
     * 3. 下载补丁并应用（当然，要看下是否开启了autoPatch）
     * 三者串行进行
     */
    fun loadPatch() {
        context?.let {
            val patchInfoJson = PPSharePreferenceHelper.getString(context, Const.SP_KEY_PATCH_INFO)
            localPatchInfo = JSON.parseObject(patchInfoJson, com.feelschaotic.sdkhotfix.sdk.entity.CheckVersionRespData::class.java)
            loadLocalPatch()
            getPatchForServer()
        }
    }


    /**
     * 如果存在本地补丁，则加载
     */
    private fun loadLocalPatch() {
        localPatchInfo?.let {
            if (it.patchCanLoad()) {
                StatisticsManager.track(StatisticsConstants.EventCode.CODE_BEGIN_PATCH)
                fixedThreadPool.execute(PatchExecutor(context, PatchManipulateImp(it, TYPE_ONLY_LOAD_LOCAL), MyRobustCallBack()))
            }
        }
    }

    private fun getPatchForServer() {
        fixedThreadPool.execute {
            ServiceManager.checkVersion(onCheckVersionListener = {
                if (it.needDownloadPatch(localPatchInfo)) {
                    StatisticsManager.track(StatisticsConstants.EventCode.CODE_NEED_DOWNLOAD)
                    LogUtils.d(TAG, "是新补丁，准备下载")
                    fixedThreadPool.execute(PatchExecutor(context, PatchManipulateImp(it, PatchManipulateImp.TYPE_FROM_SERVER), MyRobustCallBack()))
                } else {
                    LogUtils.d(TAG, "与本地补丁一致，不下载")
                }
            })
        }
    }


    class MyRobustCallBack : RobustCallBack {
        override fun onPatchListFetched(result: Boolean, isNet: Boolean, patches: List<Patch>?) {
            val msg = "获取补丁列表成功: " + "result=" + result + ";isNet=" + isNet + ";patches=" + jointPatchesName(patches)
            Log.d(TAG, msg)
        }

        override fun onPatchFetched(result: Boolean, isNet: Boolean, patch: Patch?) {
            val msg = "获取补丁成功: " + "result=" + result + ";isNet=" + isNet + ";patch=" + if (patch == null) "null" else patch.name
            Log.d(TAG, msg)
        }

        override fun onPatchApplied(result: Boolean, patch: Patch?) {
            val msg = "补丁应用成功: " + "result=" + result + ";patch=" + if (patch == null) "null" else patch.name
            StatisticsManager.track(StatisticsConstants.EventCode.CODE_PATCH_RESULT, result.toString(), patch.toString())
            Log.d(TAG, msg)
        }

        override fun logNotify(log: String, where: String) {
            val msg = "RobustCallBack提示log: log=$log;where=$where"
            StatisticsManager.track(StatisticsConstants.EventCode.CODE_PATCH_LOG, null, where, log)
            Log.i(TAG, msg)
        }

        override fun exceptionNotify(throwable: Throwable, where: String) {
            val msg = "RobustCallBack提示异常: " + "throwable=" + throwable.message + ";where=" + where
            StatisticsManager.track(StatisticsConstants.EventCode.CODE_PATCH_CATCH, null, where, throwable.localizedMessage)
            Log.e(TAG, msg)
        }
    }

    private fun jointPatchesName(patches: List<Patch>?): String {
        if (patches == null || patches.isEmpty()) {
            return ""
        }
        val sb = StringBuilder(patches.size)
        for (p in patches) {
            sb.append(p.name).append(",")
        }
        return sb.toString()
    }
}