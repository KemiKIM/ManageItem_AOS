package com.seongho.manageitem.features.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.concurrent.TimeUnit

object GoogleADManager {

    private const val TAG = "GoogleADManager"

    // 실제 운영 시에는 본인의 광고 단위 ID로 교체하세요.
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // 테스트 ID

    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading: Boolean = false


    /**
     * 전면 광고 로드
     */
    fun loadInterstitialAd(context: Context) {
        if (isAdLoading || mInterstitialAd != null) {
            Log.d(TAG, "광고가 이미 로드 중이거나 로드되어 있습니다.")
            return
        }

        isAdLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "전면 광고 로드 실패: ${adError.message}")
                    isAdLoading = false
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "전면 광고 로드 성공.")
                    isAdLoading = false
                    mInterstitialAd = interstitialAd
                }
            }
        )
    }

    /**
     * 전면 광고 표시
     * @param activity 광고를 표시할 Activity
     * @param onAdDismissed 광고가 닫혔을 때 호출될 콜백
     * @param onAdShowFailed 광고 표시에 실패했을 때 호출될 콜백 (선택 사항)
     */
    fun showInterstitialAd(
        activity: Activity,
        onAdDismissed: () -> Unit,
        onAdShowFailed: (() -> Unit)? = null // 실패 콜백 추가
    ) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "전면 광고 닫힘.")
                    mInterstitialAd = null // 광고를 한 번 사용하면 다시 로드해야 함
                    loadInterstitialAd(activity.applicationContext) // 다음 광고 미리 로드

                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "전면 광고 표시 실패: ${adError.message}")
                    mInterstitialAd = null
                    onAdShowFailed?.invoke() ?: onAdDismissed() // 실패 시에도 onAdDismissed 호출 (기본 동작)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "전면 광고 표시됨.")
                    // lastAdShownTimestamp = System.currentTimeMillis() // 마지막 표시 시간 기록 (간격 제어시)
                }

                override fun onAdImpression() {
                    Log.d(TAG, "전면 광고 노출 (Impression).")
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            Log.d(TAG, "전면 광고가 로드되지 않았습니다. 닫힘 콜백을 바로 실행합니다.")
            // 광고가 없다면, 광고를 기다리지 않고 바로 다음 로직 수행
            onAdDismissed()
        }
    }
}