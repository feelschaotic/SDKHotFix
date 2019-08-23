package com.feelschaotic.sdkhotfix.sdk.service

import android.app.Application
import com.alibaba.sdk.android.oss.*
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider
import com.alibaba.sdk.android.oss.model.GetObjectRequest
import com.feelschaotic.sdkhotfix.sdk.BuildConfig
import com.feelschaotic.sdkhotfix.sdk.HotfixManager
import com.feelschaotic.sdkhotfix.sdk.entity.DownloadRequest
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * 阿里云oss文件下载
 * @author feelschaotic
 * @create 2019/6/26.
 */
object FileDownloader {
    private val TAG = "sdk-patch"
    private var ossClient: OSS? = null

    init {
        HotfixManager.getApplication()?.let {
            initOSSClient(it)
        }
    }

    private fun initOSSClient(application: Application) {
        val credentialProvider = OSSPlainTextAKSKCredentialProvider(BuildConfig.ACCESS_ID, BuildConfig.SECRET_KEY)
        val conf = ClientConfiguration()
        conf.maxErrorRetry = 3
        OSSLog.enableLog()
        ossClient = OSSClient(application, BuildConfig.END_POINT, credentialProvider, conf)
    }

    /**
     * oss流式下载
     */
    fun downloadSync(request: DownloadRequest, listener: RespondListener<String>) {
        if (ossClient == null) {
            LogUtils.e(TAG, "初始化阿里云OSS-SDK失败，下载终止")
            return
        }

        val dirPathFile = File(request.fileDir)
        if (!dirPathFile.exists() && !dirPathFile.mkdirs()) {
            LogUtils.e(TAG, "创建补丁下载目录失败，下载终止")
            return
        }

        val downloadRequest = GetObjectRequest(request.bucketName, request.objectKey)
        var inputStream: InputStream? = null
        var outStream: FileOutputStream? = null
        val fileLocalPath = request.fileDir + File.separator + request.patchName
        try {
            val result = ossClient!!.getObject(downloadRequest)
            inputStream = result.objectContent
            val buffer = ByteArray(2048)
            var len: Int
            outStream = FileOutputStream(fileLocalPath)
            do {
                len = inputStream.read(buffer)
                if (len != -1) {
                    outStream.write(buffer, 0, len)
                } else {
                    break
                }
            } while (true)

            listener.onSuccess(fileLocalPath)
        } catch (e: ClientException) {
            // 本地异常如网络异常等
            listener.onError(e)
        } catch (e: ServiceException) {
            // 服务异常
            listener.onError(e)
        } catch (e: IOException) {
            listener.onError(e)
        } finally {
            outStream?.close()
            inputStream?.close()
        }
    }
}