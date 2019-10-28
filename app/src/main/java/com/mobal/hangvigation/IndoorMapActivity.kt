package com.mobal.hangvigation

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_indoor_map.*

class IndoorMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_indoor_map)

        prt.addView(InnerMapView(this, BitmapFactory.decodeResource(resources, R.drawable.f3)))
    }
}
