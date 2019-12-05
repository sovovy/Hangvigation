package com.mobal.hangvigation.ui.indoor_info

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mobal.hangvigation.R
import com.mobal.hangvigation.model.GetInfoResponse
import com.mobal.hangvigation.model.GetInfoResponseData
import com.mobal.hangvigation.network.ApplicationController
import com.mobal.hangvigation.network.NetworkService
import kotlinx.android.synthetic.main.activity_indoor_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IndoorInfoActivity : AppCompatActivity() {
    private var networkService : NetworkService = ApplicationController.instance.networkService
    private var placeIdx : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_indoor_info)

        placeIdx = intent.getIntExtra("INDOOR_PLACE_IDX", 1)

        communication()
    }

    private fun communication() {
        val getInfo = networkService.getInfo(placeIdx)
        getInfo.enqueue(object: Callback<GetInfoResponse>{
            override fun onFailure(call: Call<GetInfoResponse>?, t: Throwable?) {
                Log.d("Error::Info", "$t")
            }

            override fun onResponse(call: Call<GetInfoResponse>?, response: Response<GetInfoResponse>?) {
                if (response!!.isSuccessful){
                    setInfoUI(response.body().data)
                }
            }

        })
    }

    private fun setInfoUI(data: GetInfoResponseData) {
        tv_name_indoor_info.text = data.name?: "${data.building} ${data.num}호"
        tv_position_indoor_info.text = "${data.building} ${data.floor} ${data.num}호"
        if (data.tag1!=null) {
            tv_tag1_indoor_info.text = data.tag1
            tv_tag1_indoor_info.visibility = View.VISIBLE
        }
        if (data.tag2!=null) {
            tv_tag2_indoor_info.text = data.tag2
            tv_tag2_indoor_info.visibility = View.VISIBLE
        }
        if (data.tag3!=null) {
            tv_tag3_indoor_info.text = data.tag3
            tv_tag3_indoor_info.visibility = View.VISIBLE
        }
        tv_detail_info.text = data.info?:"상세 정보가 없습니다."
    }
}
