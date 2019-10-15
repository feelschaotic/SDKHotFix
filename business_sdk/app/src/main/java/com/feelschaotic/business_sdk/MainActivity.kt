package com.feelschaotic.business_sdk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.feelschaotic.samplesdk.SdkManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init_btn.setOnClickListener {
            Log.d("TAG", "初始化业务sdk")
            SdkManager.init(application)
        }

        fix_btn.setOnClickListener {
            Log.d("TAG", "初始化hotfix sdk")
            SdkManager.fixBug()
        }

        call_bug_btn.setOnClickListener {
            Log.d("TAG", "调用业务sdk有bug的方法")
            SdkManager.callBug()
        }

        call_bug_again_btn.setOnClickListener {
            Log.d("TAG", "调用业务sdk有bug的方法")
            SdkManager.callBug()
        }
    }
}
