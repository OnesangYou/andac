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
    private val imageAndNumList = arrayListOf(
            Pair(R.drawable.test_1, 74), Pair(R.drawable.test_2, 26), Pair(R.drawable.test_3, 8), Pair(R.drawable.test_4, 12),
            Pair(R.drawable.test_5, 97), Pair(R.drawable.test_6, 6), Pair(R.drawable.test_7, 15), Pair(R.drawable.test_8, 5),
            Pair(R.drawable.test_9, 6), Pair(R.drawable.test_10, 7), Pair(R.drawable.test_11, 57), Pair(R.drawable.test_12, 10),
            Pair(R.drawable.test_13, 16), Pair(R.drawable.test_14, 29), Pair(R.drawable.test_15, 73), Pair(R.drawable.test_16, 29))
    private lateinit var btnList: ArrayList<Button>
    private lateinit var binding: ActivityEyeTestBinding

    private var progressCount = 0
    private var correctNum = 0
    private var missCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eye_test)
        prepareUi()
        setupEvents()
    }

    private fun prepareUi() {
        setActionBarLeftImage(R.drawable.back)
        hideActionBarRight()
        binding = getBinding()
        btnList = arrayListOf(binding.btnEyeTestBtn1, binding.btnEyeTestBtn2, binding.btnEyeTestBtn3)

        showRandom()
    }

    private fun showRandom() {
        progressCount++
        var shuffledList = (0 until imageAndNumList.size).shuffled()
        correctNum = shuffledList[0]
        val miss1 = shuffledList[1]
        val miss2 = shuffledList[2]
        binding.imgview.loadImageAny(imageAndNumList[correctNum].first)
        shuffledList = mutableListOf(correctNum, miss1, miss2).shuffled()
        mutableListOf(binding.btnEyeTestBtn1, binding.btnEyeTestBtn2, binding.btnEyeTestBtn3)
                .forEachIndexed { index, button -> button.text = imageAndNumList[shuffledList[index]].second.toString() }
    }

    private fun setupEvents() {
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })
        val onClickListener = View.OnClickListener {
            if (it is TextView) {
                if (it.text.toString().toInt() != imageAndNumList[correctNum].second) {
                    missCount++
                }
                if (progressCount < 8) {
                    showRandom()
                } else {
                    AlertDialog.Builder(this).apply {
                        setMessage(if (missCount == 0) getString(R.string.eye_test_success) else getString(R.string.eye_test_failure, missCount))
                        setPositiveButton("확인") { dialogInterface, which ->
                            dialogInterface.dismiss()
                            finish()
                        }
                        show()
                    }
                }
            }
        }
        binding.btnEyeTestBtn1.setOnClickListener(onClickListener)
        binding.btnEyeTestBtn2.setOnClickListener(onClickListener)
        binding.btnEyeTestBtn3.setOnClickListener(onClickListener)
    }
}
