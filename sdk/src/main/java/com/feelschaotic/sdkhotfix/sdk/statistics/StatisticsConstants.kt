package com.feelschaotic.sdkhotfix.sdk.statistics

class StatisticsConstants {
    companion object Event {
        // 上报事件
        const val EVENT_KEY = "event"
        // 上报事件场景
        const val EVENT_SCENE = "scene"
        // 上报事件描述
        const val EVENT_EXTRAS = "extras"
        // 上报事件附加码
        const val EXTRA_CODE = "extraCode"
        // 上报事件附加描述
        const val EXTRA_DESC = "desc"
    }

    object EventCode {
        //接口请求
        const val CODE_REQUEST = 1

        // 接口请求成功
        const val CODE_REQUEST_SUCCESS = 2

        // 接口请求失败
        const val CODE_REQUEST_ERROR = 3

        // 请求到了新补丁
        const val CODE_REQUEST_NEW_PATCH = 4

        // 有新补丁需要下载时
        const val CODE_NEED_DOWNLOAD = 5

        // 下载补丁成功
        const val CODE_DOWNLOAD_SUCCESS = 6

        // 下载补丁失败
        const val CODE_DOWNLOAD_ERROR = 7


        // patch结果埋点
        const val CODE_PATCH_RESULT = 8

        // 补丁发生异常事件触发回滚
        const val CODE_ON_CATCH = 9

        // 补丁回滚了的方法被调用
        const val CODE_ROLLBACK_STATE = 10

        // 补丁解密事件
        const val CODE_DECODE = 11

        // 补丁解密失败
        const val CODE_DECODE_ERROR = 12

        // 补丁解密成功
        const val CODE_DECODE_SUCCESS = 13

        // 尝试patch
        const val CODE_BEGIN_PATCH = 14
        // patch提示log
        const val CODE_PATCH_LOG = 15
        // patch提示异常
        const val CODE_PATCH_CATCH = 16
    }
}