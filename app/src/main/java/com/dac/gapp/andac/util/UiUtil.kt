package com.dac.gapp.andac.util

import android.view.View
import android.view.animation.AlphaAnimation
import com.google.firebase.auth.*

class UiUtil {
    companion object {
        fun visibleOrGone(visibleOrGone: Boolean, vararg views: View?) {
            for (view in views) {
                view?.visibility = if (visibleOrGone) View.VISIBLE else View.GONE
            }
        }

        val VisionArr = DoubleArray(30) { i->i*0.1}.map { "%.1f".format(it) }.toTypedArray()
        fun getVisionIndex(vision : Double) : Int {
            VisionArr.forEachIndexed { index, s ->
                if(s == "%.1f".format(vision)) return index
            }
            return 10
        }

        fun getMessageFromAuthException(exception: FirebaseAuthException) =
                when (exception) {
                    is FirebaseAuthWeakPasswordException -> "잘못된 이메일주소를 입력하셨습니다"
                    is FirebaseAuthInvalidCredentialsException -> "잘못된 패스워드 혹은 인증코드를 입력하셨습니다"
                    is FirebaseAuthUserCollisionException -> "이미 존재하는 이메일입니다"
                    is FirebaseAuthActionCodeException -> "유효기간이 만료되었습니다"
                    is FirebaseAuthInvalidUserException -> "비활성화된 계정입니다"
                    is FirebaseAuthRecentLoginRequiredException -> "자격증명이 유효하지 않습니다"
                    else -> "등록에 실패하였습니다"
                }

        val AdminEmail = "ndactor123@gmail.com"

        val ScrollRefreshTriggerRatio = 0.3

        private val FADE_DURATION : Long = 300
        fun setFadeAnimation(view: View) {
            val anim = AlphaAnimation(0.0f, 1.0f)
            anim.duration = FADE_DURATION
            view.startAnimation(anim)
        }
    }


}