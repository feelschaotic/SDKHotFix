package com.feelschaotic.sdkhotfix

import android.app.Activity
import android.os.Bundle
import com.feelschaotic.sdkhotfix.sdk.HotfixConfig
import com.feelschaotic.sdkhotfix.sdk.HotfixManager
import com.feelschaotic.sdkhotfix.sdk.service.ServiceManager
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author feelschaotic
 * @create 2019/6/04.
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fix_btn.setOnClickListener {
            initHotfix()
        }
    }

    private fun initHotfix() {
        val config: HotfixConfig = HotfixConfig.Builder().debug(true)
                .appVersion(BuildConfig.VERSION_NAME)
                .packageName(BuildConfig.APPLICATION_ID)
                .build()
        HotfixManager.init(application, config)

        ServiceManager.checkVersion(onCheckVersionListener = {
            LogUtils.d("TAG", "result:$it")
        })

    }
}