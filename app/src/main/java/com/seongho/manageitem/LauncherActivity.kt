package com.seongho.manageitem

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 스플래시 로직, 초기화 로직 등
        // 작업 완료 후 MainActivity로 이동

        startActivity(Intent(this, MainActivity::class.java))
        finish() // LauncherActivity는 종료하여 백스택에 남지 않도록 함
    }
}
