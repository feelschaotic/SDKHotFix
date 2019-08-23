package com.feelschaotic.sdkhotfix.sdk.service

import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsConstants
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsManager
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

/**
 * @author feelschaotic
 * @create 2019/6/27.
 */
class RetryInterceptor : Interceptor {
    companion object {
        const val TAG = "sdk-patch"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val retryWrapper = proceed(chain)
        while (retryWrapper.isNeedReTry()) {
            retryWrapper.retry(chain)
        }
        return retryWrapper.response ?: chain.proceed(chain.request())
    }

    private fun proceed(chain: Interceptor.Chain): RetryWrapper {
        val request = chain.request()
        val retryWrapper = RetryWrapper(request)
        retryWrapper.proceed(chain, request, retryWrapper)
        return retryWrapper
    }

    class RetryWrapper(private var request: Request) {
        var retryNum = 0
        var response: Response? = null
        // 重试次数
        private val MAX_RETRY_TIME = 3
        // 延迟
        private val delay: Long = 3000
        // 叠加延迟
        private val increaseDelay: Long = 5000

        fun isSuccessful(): Boolean {
            return response != null && response!!.isSuccessful
        }

        fun isNeedReTry(): Boolean {
            return !isSuccessful() && retryNum < MAX_RETRY_TIME
        }

        fun retry(chain: Interceptor.Chain) {
            retryNum++
            LogUtils.d(TAG, "RetryInterceptor 重试第" + retryNum + "次")
            try {
                Thread.sleep(delay + (retryNum - 1) * increaseDelay)
            } catch (e: InterruptedException) {
                LogUtils.printError(e)
            }
            proceed(chain, request, this)
        }

        fun proceed(chain: Interceptor.Chain, request: Request, retryWrapper: RetryWrapper) {
            val path = request.url().url().path
            var response: Response? = null
            try {
                LogUtils.d(TAG, "url:$path")
                StatisticsManager.track(StatisticsConstants.EventCode.CODE_REQUEST, path)

                response = chain.proceed(request)
                LogUtils.d(TAG, "response:$response")
                retryWrapper.response = response
                StatisticsManager.track(StatisticsConstants.EventCode.CODE_REQUEST_SUCCESS, path)
            } catch (e: SocketException) {
                onRequestError(e, path, response?.code(), response?.message())
            } catch (e: SocketTimeoutException) {//超时异常
                onRequestError(e, path, response?.code(), response?.message())
            } catch (e: ConnectException) {//连接异常
                onRequestError(e, path, response?.code(), response?.message())
            }
        }

        private fun onRequestError(e: Exception, path: String, code: Int?, message: String?) {
            StatisticsManager.track(StatisticsConstants.EventCode.CODE_REQUEST_ERROR, path, code?.toString(), message)
            LogUtils.printError(e)
        }
    }

}

