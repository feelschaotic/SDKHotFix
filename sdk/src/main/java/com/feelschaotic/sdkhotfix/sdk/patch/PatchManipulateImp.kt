package com.feelschaotic.sdkhotfix.sdk.patch

import android.content.Context
import android.util.Base64
import com.alibaba.fastjson.JSON
import com.meituan.robust.Patch
import com.meituan.robust.PatchManipulate
import com.feelschaotic.crypto.crypto.AESUtil
import com.feelschaotic.crypto.crypto.EncryptManager
import com.feelschaotic.sdkhotfix.sdk.HotfixManager
import com.feelschaotic.sdkhotfix.sdk.const.Const
import com.feelschaotic.sdkhotfix.sdk.entity.CheckVersionRespData
import com.feelschaotic.sdkhotfix.sdk.service.RespondListener
import com.feelschaotic.sdkhotfix.sdk.service.ServiceManager
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsConstants
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsManager
import com.feelschaotic.sdkhotfix.sdk.utils.ByteUtil
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils
import com.feelschaotic.sdkhotfix.sdk.utils.PPSharePreferenceHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch

/**
 * 补丁管理类
 *
 * 继承PatchManipulate实现补丁加载策略
 * 在这个类中负责补丁的下载、校验和使用策略等
 * @author feelschaotic
 * @create 2019/6/4.
 */

class PatchManipulateImp(private val mPatchInfo: CheckVersionRespData,
                         private val mLocalType: Int) : PatchManipulate() {

    companion object {
        const val TYPE_ONLY_LOAD_LOCAL = 1
        const val TYPE_FROM_SERVER = 2
        const val IMPL_CLASS_FULL_NAME = "com.feelschaotic.sdkhotfix.sdk.PatchManipulateImp.PatchesInfoImpl"
    }

    private val TAG = "sdk-patch"
    private lateinit var PATCH_LOCAL_PATH: String
    private lateinit var PATCH_CACHE_PATH: String
    private val PATCH_NAME = "patch.jar"

    private fun init(context: Context) {
        //使用私有目录无需权限
        PATCH_LOCAL_PATH = context.filesDir.absolutePath + File.separator + HotfixManager.config.packageName + File.separator + HotfixManager.config.appVersion
        PATCH_CACHE_PATH = context.cacheDir.absolutePath + File.separator + HotfixManager.config.packageName + File.separator + HotfixManager.config.appVersion
    }

    /**
     * 服务器应该返回的数据包括补丁文件的下载链接、补丁的md5、补丁的编号等信息
     *
     * @param context
     * @return 联网获取补丁列表，
     */
    override fun fetchPatchList(context: Context): List<Patch>? {
        init(context)
        val patches: MutableList<Patch> = mutableListOf()
        if (isOnlyFromLocal()) {
            addPatchToList(mPatchInfo.patchPath, patches)
            return patches
        }
        return loadFromServer(context)
    }

    /**
     * 下载服务端的补丁
     */
    private fun loadFromServer(context: Context): List<Patch> {
        val patches = mutableListOf<Patch>()
        // 由于是异步的网络下载补丁，所以使用CountDownLatch进行同步
        val mCountDownLatch = CountDownLatch(1)

        ServiceManager.downloadPatch(mPatchInfo.bundleurl, PATCH_LOCAL_PATH, PATCH_NAME, object : RespondListener<String> {
            override fun onSuccess(response: String) {
                StatisticsManager.track(StatisticsConstants.EventCode.CODE_DOWNLOAD_SUCCESS, mPatchInfo.bundleurl)
                LogUtils.d(TAG, "下载补丁成功，response:$response")
                mPatchInfo.patchPath = response

                HotfixManager.getApplication()?.let {
                    PPSharePreferenceHelper.putString(it, Const.SP_KEY_PATCH_INFO, JSON.toJSONString(mPatchInfo))
                }
                HotfixManager.notifyPatchUpdated()

                if (mPatchInfo.patchCanLoad()) {
                    addPatchToList(mPatchInfo.patchPath, patches)
                }
                mCountDownLatch.countDown()
            }

            override fun onError(e: Exception) {
                StatisticsManager.track(StatisticsConstants.EventCode.CODE_DOWNLOAD_ERROR, mPatchInfo.bundleurl, null, e.localizedMessage)
                LogUtils.e(TAG, "下载补丁失败")
                LogUtils.printError(e)
                mCountDownLatch.countDown()
            }
        })

        try {
            // 阻塞等待网络请求结束
            mCountDownLatch.await()
        } catch (e: InterruptedException) {
            LogUtils.printError(e)
        }

        return patches
    }

    private fun isOnlyFromLocal(): Boolean {
        return mLocalType == TYPE_ONLY_LOAD_LOCAL
    }

    /**
     * 把补丁信息填充到列表中
     * @param patchPath
     * @param patches
     */
    private fun addPatchToList(patchPath: String, patches: MutableList<Patch>) {
        StatisticsManager.track(StatisticsConstants.EventCode.CODE_DECODE, null, null, mPatchInfo.pKey)
        //解密下发的加密补丁
        val pKey = mPatchInfo.pKey
        try {
            val decryptKey = EncryptManager.getInstance().decryptByRsaPrivateKey(Base64.decode(pKey, Base64.NO_WRAP))
            val fileBytes = ByteUtil.fileToBytes(patchPath) ?: return
            val decryptBytes = AESUtil.decryptByAes(fileBytes, decryptKey)
            ByteUtil.bytesToFile(decryptBytes, PATCH_CACHE_PATH, "patch_temp.jar")
        } catch (e: java.lang.Exception) {
            LogUtils.e(TAG, "【警告】解密失败！！")
            LogUtils.printError(e)
            StatisticsManager.track(StatisticsConstants.EventCode.CODE_DECODE_ERROR, null, null, e.localizedMessage)
        }


        val patchFile = File(patchPath)
        if (patchFile.exists()) {
            StatisticsManager.track(StatisticsConstants.EventCode.CODE_DECODE_SUCCESS)

            val patch = Patch()
            patch.name = replaceJarSuffix(patchFile.name)
            /**
             * localPath设置补丁的原始路径，这个路径存储的补丁是加密过的
             * tempPath存储解密之后的补丁，是可以执行的jar文件
             * tempPath下的补丁加载完立即删除
             */
            patch.localPath = replaceJarSuffix(patchFile.path)
            patch.tempPath = PATCH_CACHE_PATH + File.separator + replaceJarSuffix(PATCH_NAME)
            LogUtils.d(TAG, "解密后补丁：" + patch.tempPath)
            patch.patchesInfoImplClassFullName = IMPL_CLASS_FULL_NAME
            patches.add(patch)
            StatisticsManager.track(StatisticsConstants.EventCode.CODE_BEGIN_PATCH, mPatchInfo.bundleversion.toString(), patchPath)
            LogUtils.d(TAG, "补丁存在：$patchPath")
        } else {
            LogUtils.e(TAG, "补丁不存在：$patchPath")
        }
    }

    private fun replaceJarSuffix(str: String): String {
        return str.replace(".jar", "")
    }

    override fun verifyPatch(context: Context, patch: Patch): Boolean {
        return true
    }

    override fun ensurePatchExist(patch: Patch): Boolean {
        return true
    }

}
