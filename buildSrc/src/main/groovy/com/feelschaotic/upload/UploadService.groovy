package com.feelschaotic.upload

import com.alibaba.fastjson.JSON
import com.aliyun.oss.ClientException
import com.aliyun.oss.OSSClient
import com.aliyun.oss.ServiceException
import okhttp3.*

import java.util.concurrent.TimeUnit

class UploadService {
    OkHttpClient client

    UploadService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
    }

    def updatePatchInfo(String url, Patch patchInfo, OnResponseListener<String> listener) {
        try {
            def requestJson = JSON.toJSONString(patchInfo)
            def request = new Request.Builder().url(url).post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), requestJson)).build()
            println "--发起请求 url:${url} \n requestJson:${requestJson}"
            def response = client.newCall(request).execute()
            if (listener != null) {
                listener.onSuccess(response.body().string())
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(e)
            }
        }
    }

    def uploadPatchToAliYun(OssConfig ossConfig, File patchFile, OnResponseListener<String> listener) {
        OSSClient ossClient = new OSSClient(ossConfig.endpoint, ossConfig.accessKeyId, ossConfig.accessKeySecret)

        println "--开始上传补丁 patchPath:${patchFile.path}"
        def objectName = System.currentTimeMillis() + "-android"
        try {
            def putResult = ossClient.putObject(ossConfig.buckName, objectName, patchFile)
            println "--上传补丁成功 ETag:${putResult.getETag()};RequestId:${putResult.getRequestId()}"
            if (listener != null) {
                listener.onSuccess(objectName)
            }
        } catch (ClientException e) {
            if (listener != null) {
                listener.onError(e)
            }
        } catch (ServiceException e) {
            if (listener != null) {
                listener.onError(e)
            }
        }
        ossClient.shutdown()
    }
}

interface OnResponseListener<T> {
    def onSuccess(T response)

    def onError(Exception e)
}