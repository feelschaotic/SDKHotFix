package com.feelschaotic.sdkhotfix.sdk

import android.app.Application
import com.alibaba.fastjson.JSON
import com.feelschaotic.sdkhotfix.sdk.const.Const
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsConstants
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsManager
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils
import com.feelschaotic.sdkhotfix.sdk.utils.PPSharePreferenceHelper
import com.meituan.robust.RollbackListener
import com.meituan.robust.RollbackManager
import java.lang.ref.WeakReference


/**
 * 热更门面类

 * @author feelschaotic
 * *
 * @create 2019/6/5.
 */

object HotfixManager {

    private lateinit var applicationRef: WeakReference<Application>
    lateinit var config: HotfixConfig
    private lateinit var rollBacks: MutableMap<String, Boolean>
    private const val TAG = "sdk-patch"
    /**
     * 初始化，检查是否有补丁
     */
    fun init(application: Application, config: HotfixConfig) {
        applicationRef = WeakReference(application)
        HotfixManager.config = config
        HotfixManager.config.checkConfig()
        initRollbackListener()
        //TODO:PatchManager.loadPatch()
    }

    private fun initRollbackListener() {

        var rollBacksJsonStr: String? = ""
        getApplication()?.let {
            rollBacksJsonStr = PPSharePreferenceHelper.getString(it, Const.SP_KEY_PATCH_ROLLBACK)
        }

        rollBacks = if (rollBacksJsonStr.isNullOrEmpty()) {
            mutableMapOf()
        } else {
            JSON.parseObject(rollBacksJsonStr, MutableMap::class.java) as MutableMap<String, Boolean>
        }

        RollbackManager.getInstance().setRollbackListener(object : RollbackListener {
            override fun onRollback(methodsId: String, methodsLongName: String, e: Throwable?) {
                StatisticsManager.track(StatisticsConstants.EventCode.CODE_ON_CATCH, methodsLongName, methodsId, e?.localizedMessage)
                LogUtils.e(TAG, "补丁$methodsId 发生异常，执行回滚！")

                rollBacks[methodsId] = true
                getApplication()?.let {
                    PPSharePreferenceHelper.putString(it, Const.SP_KEY_PATCH_ROLLBACK, JSON.toJSONString(rollBacks))
                }
            }

            override fun getRollback(methodsId: String): Boolean {
                val rollback = rollBacks[methodsId] ?: false
                LogUtils.d(TAG, "获取补丁$methodsId 的回滚状态为：$rollback")
                StatisticsManager.track(StatisticsConstants.EventCode.CODE_ROLLBACK_STATE, null, methodsId, rollback.toString())
                return rollback
            }
        })
    }

    fun getApplication(): Application? {
        return applicationRef.get()
    }

    /**
     * 当有新补丁时清空rollbacks，所有标记位置为不回滚
     */
    fun notifyPatchUpdated() {
        getApplication()?.let {
            rollBacks.clear()
            PPSharePreferenceHelper.putString(it, Const.SP_KEY_PATCH_ROLLBACK, JSON.toJSONString(rollBacks))
        }
    }

}

