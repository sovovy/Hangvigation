package com.mobal.hangvigation.ui.summary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.mobal.hangvigation.R
import kotlinx.android.synthetic.main.activity_summary.*
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class SummaryActivity : AppCompatActivity() {
    private var markerID = ""
    private var latitude : Double = 0.0
    private var longtitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        markerID = intent.getStringExtra("markerID")
        markerID = markerID.slice(IntRange(markerID.indexOf('@'), markerID.length-1))
        Log.d("marker", markerID)
//        latitude = intent.getDoubleExtra("lat")
//        longtitude = intent.getDoubleExtra("lon")

//        tv_title_summary.text
    }
}
