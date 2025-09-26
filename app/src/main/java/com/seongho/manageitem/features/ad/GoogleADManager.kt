package com.seongho.manageitem.features.ad

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.concurrent.TimeUnit

object InterstitialAdManager {

    private var sdkInterstitialAd: Any? = null // 실제 SDK의 광고 객체를 담을 변수 (타입은 Any로 임시 지정)
    private var isAdLoading: Boolean = false
    private var lastAdShownTimestamp: Long = 0
    // 광고 표시 최소 간격 (예: 2분) - 필요에 따라 조절
    private val AD_SHOW_INTERVAL_MS = TimeUnit.MINUTES.toMillis(2)

    /**
     * SDK 초기화 (Application 클래스나 MainActivity onCreate에서 한 번 호출)
     */
    fun initializeSdk(context: Context) {
        // TODO: 실제 광고 SDK 초기화 코드 작성 (예: MobileAds.initialize(context) {})
        println("InterstitialAdManager: SDK 초기화 시도 (실제 SDK 로직 필요)")
    }

    /**
     * 전면 광고 로드 요청
     * 실제 SDK에서는 비동기로 로드되므로 콜백 처리가 필요합니다.
     */
    fun loadAd(context: Context) {
        if (isAdLoading || sdkInterstitialAd != null) {
            println("InterstitialAdManager: 광고가 이미 로드 중이거나 로드된 상태입니다.")
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAdShownTimestamp < AD_SHOW_INTERVAL_MS) {
            println("InterstitialAdManager: 마지막 광고 표시 후 ${AD_SHOW_INTERVAL_MS / 60000}분이 지나지 않아 새 광고를 로드하지 않습니다.")
            return
        }

        isAdLoading = true
        println("InterstitialAdManager: 전면 광고 로드 요청 시작 (실제 SDK 로직 필요)")
        // TODO: 실제 광고 SDK의 광고 로드 코드 작성
        // 예시: AdRequest adRequest = new AdRequest.Builder().build();
        //      InterstitialAd.load(context,"YOUR_AD_UNIT_ID", adRequest, new InterstitialAdLoadCallback() {
        //          @Override
        //          public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
        //              sdkInterstitialAd = interstitialAd;
        //              isAdLoading = false;
        //              println("InterstitialAdManager: 광고 로드 성공");
        //          }
        //          @Override
        //          public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
        //              sdkInterstitialAd = null;
        //              isAdLoading = false;
        //              println("InterstitialAdManager: 광고 로드 실패: " + loadAdError.getMessage());
        //          }
        //      });

        // --- SDK 없이 테스트하기 위한 임시 로드 성공 처리 (2초 후) ---
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdLoading) { // 다른 곳에서 이미 로드/실패 처리되지 않았다면
                println("InterstitialAdManager: (임시) 광고 로드 성공 처리됨")
                sdkInterstitialAd = Any() // 광고가 로드된 것처럼 임시 객체 할당
                isAdLoading = false
            }
        }, 2000)
        // --- 임시 처리 끝 ---
    }

    /**
     * 전면 광고 표시 시도
     * @param activity 광고를 표시할 현재 Activity
     * @param onAdDismissed 광고가 닫히거나, 표시되지 않았을 때 호출될 콜백. 화면 전환 등 다음 로직 진행.
     */
    fun showAd(activity: Activity, onAdDismissed: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (sdkInterstitialAd != null && (currentTime - lastAdShownTimestamp >= AD_SHOW_INTERVAL_MS)) {
            println("InterstitialAdManager: 전면 광고 표시 시도 (실제 SDK 로직 필요)")
            // TODO: 실제 광고 SDK의 광고 표시 코드 작성
            // 예시: sdkInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            //          @Override
            //          public void onAdDismissedFullScreenContent() {
            //              println("InterstitialAdManager: 광고 닫힘");
            //              sdkInterstitialAd = null; // 광고는 일반적으로 일회용
            //              loadAd(activity.applicationContext); // 다음 광고 미리 로드
            //              onAdDismissed.invoke();
            //          }
            //          @Override
            //          public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            //              println("InterstitialAdManager: 광고 표시 실패: " + adError.getMessage());
            //              sdkInterstitialAd = null;
            //              onAdDismissed.invoke(); // 실패해도 다음 로직 진행
            //          }
            //          @Override
            //          public void onAdShowedFullScreenContent() {
            //              println("InterstitialAdManager: 광고가 화면에 표시됨");
            //              lastAdShownTimestamp = System.currentTimeMillis();
            //          }
            //      });
            //      sdkInterstitialAd.show(activity);

            // --- SDK 없이 테스트하기 위한 임시 광고 표시 및 닫힘 처리 ---
            lastAdShownTimestamp = currentTime
            sdkInterstitialAd = null // 사용된 광고는 null 처리 (일회용)
            println("InterstitialAdManager: (임시) 광고가 표시되고 1초 후 닫힘 처리됨")
            Handler(Looper.getMainLooper()).postDelayed({
                onAdDismissed.invoke()
                loadAd(activity.applicationContext) // 다음 광고 로드 시도
            }, 1000) // 사용자가 광고를 보는 시간 가정
            // --- 임시 처리 끝 ---

        } else {
            if (sdkInterstitialAd == null) {
                println("InterstitialAdManager: 표시할 광고가 로드되지 않았습니다. 로드를 시도합니다.")
                loadAd(activity.applicationContext) // 광고가 없다면 로드 시도
            } else {
                println("InterstitialAdManager: 마지막 광고 표시 후 ${AD_SHOW_INTERVAL_MS / 60000}분이 지나지 않아 광고를 표시하지 않습니다.")
            }
            onAdDismissed.invoke() // 광고를 표시하지 않았으므로 바로 다음 로직 진행
        }
    }
}