package com.seongho.manageitem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.IntentSenderRequest

import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.gms.ads.MobileAds

import android.widget.Toast
import android.os.Handler
import android.os.Looper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class LauncherActivity : ComponentActivity() {

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private lateinit var updateLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@LauncherActivity) {}
        }


        if (BuildConfig.DEBUG) {
            goToMain()

        } else {
            // 1) Activity Result 런처 등록
            updateLauncher = registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result: ActivityResult ->
                // Play Core가 리턴한 결과 처리
                if (result.resultCode == Activity.RESULT_OK) {
                    // 사용자가 업데이트 완료(또는 승인) → 보통 바로 앱 재시작 or 메인으로 이동
                    goToMain()
                } else {
                    // 사용자가 취소했거나 실패한 경우
                    this.exitAppWithDelay(msg = "업데이트 실패-0", activity = this)
                }
            }

            // 2) 업데이트 체크 시작
            checkForAppUpdate()
        }

    }



    private fun checkForAppUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            when {
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {

                    // AppUpdateOptions로 유형 지정
                    val options = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                        .setAllowAssetPackDeletion(true) // 필요시 옵션
                        .build()

                    // 새 오버로드: IntentSenderForResultStarter(=Activity Result 런처) 사용
                    try {
                        appUpdateManager.startUpdateFlow(
                            appUpdateInfo,
                            this,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                                .setAllowAssetPackDeletion(true)
                                .build()
                        )
                    } catch (e: Exception) {
                        // 시작 실패 시 안전하게 메인으로 이동
                        e.printStackTrace()
//                        goToMain()

                        Log.d("LauncherActivity", "여기이1")

                        this.exitAppWithDelay(msg = "업데이트 실패-1", activity = this)
                    }
                }

                else -> {
                    // 업데이트 없음 -> 바로 메인으로
                    goToMain()
                }
            }
        }.addOnFailureListener {
            // 체크 실패 시에도 앱은 계속 실행
//            goToMain()

            Log.d("LauncherActivity", "여기이2")

            this.exitAppWithDelay(msg = "업데이트 실패-2", activity = this)
        }
    }

    // ✅ 업데이트 완료 or 최신이면 메인으로 이동
    private fun goToMain() {
        // MainActivity로 이동
        startActivity(Intent(this, MainActivity::class.java))

        // LauncherActivity는 종료하여 백스택에 남지 않도록 함
        finish()
    }


    // onResume에서 이미 진행 중인 업데이트가 있으면 재개하도록 체크하는 것이 권장됨
    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() ==
                com.google.android.play.core.install.model.UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                // 이미 진행중인 업데이트가 있다면 재개
                val options = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                try {
                    appUpdateManager.startUpdateFlow(
                        appUpdateInfo,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                            .setAllowAssetPackDeletion(true)
                            .build()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    // 실패시 그냥 무시하고 계속 진행하거나 goToMain() 호출

                    Log.d("LauncherActivity", "여기이3")

                    this.exitAppWithDelay(msg = "업데이트 실패-3", activity = this)
                }
            }
        }
    }


    fun exitAppWithDelay(msg: String, activity: Activity, delayMillis: Long = 5000) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).postDelayed({
            activity.finishAffinity()
            exitProcess(0)
        }, delayMillis)
    }
}
