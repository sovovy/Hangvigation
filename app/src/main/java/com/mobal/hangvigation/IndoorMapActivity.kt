package com.mobal.hangvigation

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_indoor_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IndoorMapActivity : AppCompatActivity() {
    lateinit var networkService: NetworkService
    val x:Float = 0F
    val y:Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_indoor_map)

        prt.addView(InnerMapView(this, BitmapFactory.decodeResource(resources, R.drawable.f3)))


        // 네트워크
        networkService = ApplicationController.instance.networkService

        val postCoord = networkService.postCoord(arrayListOf(
            PostCoordData("06:09:b4:76:cc:d4", (-62).toDouble()),
            PostCoordData("0a:09:b4:76:cc:94", (-59).toDouble())
        )
        )

        postCoord.enqueue(object : Callback<PostCoordResponse> {
            override fun onFailure(call: Call<PostCoordResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostCoordResponse>?, response: Response<PostCoordResponse>?) {
                if(response!!.isSuccessful){
                    Log.d("ASDF", "${response.body().data.x}, ${response.body().data.y}")
                }
            }
        })

        // 새로고침 버튼 클릭
//        indoor_refresh_btn.setOnClickListener {
//            val innerMapView: InnerMapView = prt
//            innerMapView!!.x = x
//            innerMapView!!.y = y
//            innerMapView.invalidate() // 이 함수가 있어야 onDraw 호출
//        }
    }
}
