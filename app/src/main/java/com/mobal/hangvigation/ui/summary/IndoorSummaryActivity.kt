package com.mobal.hangvigation.ui.summary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.mobal.hangvigation.R
import kotlinx.android.synthetic.main.activity_summary.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class IndoorSummaryActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
        map_view_summary.visibility = View.GONE
        btn_start_summary.setOnClickListener {
            // TODO intent indoor navi
        }
    }
}
