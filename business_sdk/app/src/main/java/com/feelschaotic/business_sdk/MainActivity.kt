package com.feelschaotic.business_sdk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       /* init_btn.setOnClickListener {
            Log.d("TAG", "初始化业务sdk")
            SdkManager.init(application)
        }

        fix_btn.setOnClickListener {
            Log.d("TAG", "初始化hotfix sdk（延迟初始化，避免一进来就初始化会把补丁给打上，看不到效果）")
            SdkManager.fixBug()
        }
        call_bug_btn.setOnClickListener {
            Log.d("TAG", "调用业务sdk有bug的方法")
            SdkManager.callBug()
        }*/
    }
}
