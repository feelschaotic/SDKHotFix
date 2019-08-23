package com.feelschaotic.sdkhotfix.sdk.service

import com.feelschaotic.sdkhotfix.sdk.entity.DownloadRequest
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * service层，用于和服务端交互，纯网络请求
 * @author feelschaotic
 * @create 2019/6/5.
 */
class PatchService {
    private val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(RetryInterceptor())
            .build()

    /**
     * 从远端获取补丁列表
     */
    fun checkVersion(url: String, map: MutableMap<String, String>, listener: RespondListener<String>) {
        if (map.isEmpty()) {
            return
        }
        val finalUrl = jointUrl(url, map)
        client.newCall(Request.Builder().url(finalUrl).get().build()).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response?) {
                response?.body()?.let {
                    listener.onSuccess(it.string())
                }
            }

            override fun onFailure(call: Call, e: IOException?) {
                e?.let {
                    listener.onError(it)
                }
            }
        })
    }

    private fun jointUrl(url: String, map: MutableMap<String, String>): String {
        val sb = StringBuffer("$url?")
        for (key in map.keys) {
            sb.append(key)
            sb.append("=")
            sb.append(map[key])
            sb.append("&")
        }
        return sb.toString().substring(0, sb.toString().length - 1)
    }

    /**
     * 下载补丁
     */
    fun downloadPatch(request: DownloadRequest, listener: RespondListener<String>) {
        FileDownloader.downloadSync(request, listener)
    }

}