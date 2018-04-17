package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.dac.gapp.andac.adapter.ColumnTitleRecyclerViewAdapter
import com.dac.gapp.andac.model.columnTitle
import kotlinx.android.synthetic.main.activity_column.*

class ColumnActivity : AppCompatActivity() {
    var columntitleList : MutableList<columnTitle> = mutableListOf<columnTitle>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column)

        prepareData()
        var layoutmanager : RecyclerView.LayoutManager = GridLayoutManager(applicationContext,2)

        columnList.layoutManager = layoutmanager
        columnList.adapter = ColumnTitleRecyclerViewAdapter(applicationContext,columntitleList)



    }

    fun prepareData(){
        for(i in 1..10){

            columntitleList.add(columnTitle(1,"Dsadsa"))
        }
    }
}
