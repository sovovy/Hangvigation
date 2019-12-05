package com.mobal.hangvigation.ui.indoor_info

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.mobal.hangvigation.R
import com.mobal.hangvigation.model.GetDivisionResponse
import com.mobal.hangvigation.model.GetDivisionResponseData
import com.mobal.hangvigation.network.ApplicationController
import com.mobal.hangvigation.network.NetworkService
import kotlinx.android.synthetic.main.activity_place_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceListActivity : AppCompatActivity() {
    private var networkService : NetworkService = ApplicationController.instance.networkService
    private var divisionOrPlaceItems: ArrayList<GetDivisionResponseData> = ArrayList()
    private lateinit var divisionAdapter: DivisionAdapter
    private lateinit var placeAdapter: PlaceAdapter
    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_list)

        title = intent.getStringExtra("TITLE")
        tv_top_place.text = title

        iv_back_place.setOnClickListener { onBackPressed() }

        communication()
    }

    private fun setRecyclerDivision() {
        divisionAdapter = DivisionAdapter(this, divisionOrPlaceItems)
        rv_top_division.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_top_division.adapter = divisionAdapter
    }
    private fun setRecyclerPlace() {
        placeAdapter = PlaceAdapter(this, divisionOrPlaceItems)
        rv_place_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_place_list.adapter = placeAdapter
    }

    private fun communication() {
        val getDivision = networkService.getDivision(intent.getIntExtra("DIVISION_IDX", 2))
        getDivision.enqueue(object : Callback<GetDivisionResponse> {
            override fun onFailure(call: Call<GetDivisionResponse>?, t: Throwable?) {
                Log.d("Error::PlaceList", "$t")
            }

            override fun onResponse(call: Call<GetDivisionResponse>?, response: Response<GetDivisionResponse>?) {
                if (response!!.isSuccessful) {
                    divisionOrPlaceItems = response.body().data

                    if (response.body().data[0].division_idx != null) {
                        setRecyclerDivision()
                    } else {
                        invisibleDivisionUI()
                        setRecyclerPlace()
                    }
                }
            }
        })
    }

    private fun invisibleDivisionUI() {
        view_bottom_place_list.visibility = View.INVISIBLE
        rv_top_division.visibility = View.INVISIBLE
        tv_center_place_list.visibility = View.INVISIBLE
        iv_center_place_list.visibility = View.INVISIBLE
    }
}
