package com.feelschaotic.sdkhotfix.sdk.service

import com.alibaba.fastjson.JSON
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
    fun checkVersion(url: String, headers: Headers, map: MutableMap<String, String>, listener: RespondListener<String>) {
        if (map.isEmpty()) {
            return
        }
        val requestJson = JSON.toJSONString(map)
        client.newCall(Request.Builder()
                .url(url)
                .headers(headers)
                .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), requestJson)).build())
                .enqueue(object : Callback {
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

    /**
     * 下载补丁
     */
    fun downloadPatch(request: DownloadRequest, listener: RespondListener<String>) {
        FileDownloader.downloadSync(request, listener)
    }

}