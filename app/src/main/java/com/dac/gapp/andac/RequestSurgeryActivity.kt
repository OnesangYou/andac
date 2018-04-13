package com.dac.gapp.andac

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.dac.gapp.andac.adapter.SurgeryTypeSelectPagerAdapter
import com.dac.gapp.andac.model.SurgeryInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_request_surgery.*

class RequestSurgeryActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_surgery)


        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Get the ActionBar here to configure the way it behaves.
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        toolbar.setNavigationIcon(R.drawable.back)
        toolbar.setNavigationOnClickListener(){
            view ->onBackPressed()
        }
        val mViewPager: ViewPager = tabViwePager
        mViewPager.adapter = SurgeryTypeSelectPagerAdapter(supportFragmentManager)

        val mTabLayout: TabLayout = tablayout
        mTabLayout.setupWithViewPager(mViewPager)

        mTabLayout.getTabAt(0)!!.setText("오픈형")
        mTabLayout.getTabAt(1)!!.setText("지정형")

        val db : FirebaseFirestore = FirebaseFirestore.getInstance()

        sumitButton.setOnClickListener(){
            view -> val tag = tagText.text.toString()
                    val visualacuity = visualacuityEdit.text.toString()
                    val disease = diseaseEdit.text.toString()
                    val name = nameEdit.text.toString()
                    val phone = phoneEdit.text.toString()
                    val surgery :SurgeryInfo = SurgeryInfo(tag,visualacuity,disease,name,phone)
                    val key = db.collection("surgery").document().id
                    db.collection("surgery").document(key).set(surgery)
        }
    }
}
