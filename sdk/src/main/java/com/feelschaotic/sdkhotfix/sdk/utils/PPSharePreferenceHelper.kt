package com.feelschaotic.sdkhotfix.sdk.utils

import android.content.Context
import com.tencent.mmkv.MMKV

internal object PPSharePreferenceHelper {
    private const val SP_FILE_NAME = "hotfix_sp"
    private var mmkv: MMKV? = null

    private fun initMMKV(context: Context) {
        if (null == mmkv) {
            MMKV.initialize(context)
            mmkv = MMKV.mmkvWithID(SP_FILE_NAME)
        }
    }

    @Synchronized
    fun putString(context: Context, key: String, data: String) {
        initMMKV(context)
        mmkv!!.encode(key, data)
    }

    fun getString(context: Context, key: String, default: String? = ""): String {
        initMMKV(context)
        return mmkv!!.decodeString(key, default)
    }
}