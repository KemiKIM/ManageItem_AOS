package com.seongho.manageitem.utils

import android.content.Context
import android.widget.Toast
import java.lang.ref.WeakReference

object ToastManager {
    private var currentToast: WeakReference<Toast>? = null

    fun showToast(context: Context,
                  message: String, duration:
                  Int = Toast.LENGTH_SHORT) {
        // 이전에 보여준 토스트가 있다면 취소 (선택 사항, 중복 방지)
        currentToast?.get()?.cancel()

        val toast = Toast.makeText(context.applicationContext, message, duration) // ApplicationContext 사용
        toast.show()
        currentToast = WeakReference(toast)
    }
}