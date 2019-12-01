package com.mobal.hangvigation.ui.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mobal.hangvigation.ui.indoor_info.PlaceListActivity
import com.mobal.hangvigation.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setClickListener()
    }

    private fun setClickListener() {
        // 검색 버튼 관련 VISIBILITY 관리
        iv_search_main.setOnClickListener {
            cl_second_main.visibility = View.GONE
            cl_search_main.visibility = View.VISIBLE
        }

        iv_cancel_search.setOnClickListener {
            cl_second_main.visibility = View.VISIBLE
            cl_search_main.visibility = View.GONE
        }

        // 실내 장소 카테고리 버튼 관리
        // 강의실
        bt_first_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 2)
                startActivity(it)
            }
        }
        // 편의시설
        bt_second_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 66)
                startActivity(it)
            }
        }
        // 연구사무실
        bt_third_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 70)
                startActivity(it)
            }
        }
        // 그외
        bt_fourth_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 86)
                startActivity(it)
            }
        }
    }
}
