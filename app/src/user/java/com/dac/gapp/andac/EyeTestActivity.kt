package com.dac.gapp.andac

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityEyeTestBinding
import com.dac.gapp.andac.extension.loadImageAny

class EyeTestActivity : BaseActivity() {
    private val mImageAndNumList = arrayListOf(
            Pair(R.drawable.test_1, 74), Pair(R.drawable.test_2, 26), Pair(R.drawable.test_3, 8), Pair(R.drawable.test_4, 12),
            Pair(R.drawable.test_5, 97), Pair(R.drawable.test_6, 6), Pair(R.drawable.test_7, 15), Pair(R.drawable.test_8, 5),
            Pair(R.drawable.test_9, 6), Pair(R.drawable.test_10, 7), Pair(R.drawable.test_11, 57), Pair(R.drawable.test_12, 10),
            Pair(R.drawable.test_13, 16), Pair(R.drawable.test_14, 29), Pair(R.drawable.test_15, 73), Pair(R.drawable.test_16, 29))
    private lateinit var mBtnList: ArrayList<Button>
    private lateinit var mBinding: ActivityEyeTestBinding

    private var mProgressCount = 0
    private var mCorrectNum = 0
    private var mMissCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eye_test)
        prepareUi()
        setupEvents()
    }

    private fun prepareUi() {
        setActionBarLeftImage(R.drawable.back)
        hideActionBarRight()
        mBinding = getBinding()
        mBtnList = arrayListOf(mBinding.btnEyeTestBtn1, mBinding.btnEyeTestBtn2, mBinding.btnEyeTestBtn3)

        showRandom()
    }

    private fun showRandom() {
        mProgressCount++
        var shuffledList = (0 until mImageAndNumList.size).shuffled()
        mCorrectNum = shuffledList[0]
        val miss1 = shuffledList[1]
        val miss2 = shuffledList[2]
        mBinding.imgview.loadImageAny(mImageAndNumList[mCorrectNum].first)
        shuffledList = mutableListOf(mCorrectNum, miss1, miss2).shuffled()
        mutableListOf(mBinding.btnEyeTestBtn1, mBinding.btnEyeTestBtn2, mBinding.btnEyeTestBtn3)
                .forEachIndexed { index, button -> button.text = mImageAndNumList[shuffledList[index]].second.toString() }
    }

    private fun setupEvents() {
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })
        val onClickListener = View.OnClickListener {
            if (it is TextView) {
                if (it.text.toString().toInt() != mImageAndNumList[mCorrectNum].second) {
                    mMissCount++
                }
                if (mProgressCount < 6) {
                    showRandom()
                } else {
                    AlertDialog.Builder(this).apply {
                        title = "title 입니다."
                        setMessage(if (mMissCount == 0) "다맞으셧네요!!" else "틀린 갯수 $mMissCount!!")
                        setPositiveButton("확인") { dialogInterface, which ->
                            dialogInterface.dismiss()
                            finish()
                        }
                        show()
                    }
                }
            }
        }
        mBinding.btnEyeTestBtn1.setOnClickListener(onClickListener)
        mBinding.btnEyeTestBtn2.setOnClickListener(onClickListener)
        mBinding.btnEyeTestBtn3.setOnClickListener(onClickListener)
    }
}
