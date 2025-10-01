package com.seongho.manageitem

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent

import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@LauncherActivity) {}
        }


        // 스플래시 로직, 초기화 로직 등
        // 작업 완료 후 MainActivity로 이동
        startActivity(Intent(this, MainActivity::class.java))
        finish() // LauncherActivity는 종료하여 백스택에 남지 않도록 함
    }
}
