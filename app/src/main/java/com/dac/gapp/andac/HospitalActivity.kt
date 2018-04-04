package com.dac.gapp.andac

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

const val EXTRA_HOSPITAL_ID = "EXTRA_HOSPITAL_ID"

class HospitalActivity : AppCompatActivity() {

    // static method
    companion object {
        fun createIntent(context: Context, hospitalId: Int): Intent {
            var intent = Intent(context, HospitalActivity::class.java)
            intent.putExtra(EXTRA_HOSPITAL_ID, hospitalId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital)
        prepareUi()
    }

    private fun prepareUi() {
        val hospitalId = intent.getIntExtra(EXTRA_HOSPITAL_ID, 0)
        Toast.makeText(this, "hospitalId: $hospitalId", Toast.LENGTH_SHORT).show()
    }
}
