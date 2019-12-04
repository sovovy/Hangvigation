package com.mobal.hangvigation.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.kakao.util.maps.helper.Utility
import com.mobal.hangvigation.ui.indoor_info.PlaceListActivity
import com.mobal.hangvigation.R
import com.mobal.hangvigation.ui.indoor_navi.IndoorMapActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {
    private val MARKER_POINT_SCIENCE = MapPoint.mapPointWithGeoCoord(37.601532, 126.865048) // 과학관
    private val MARKER_POINT_MECHANICAL = MapPoint.mapPointWithGeoCoord(37.601323, 126.864513)// 기계관
    private val MARKER_POINT_ELECTRONIC = MapPoint.mapPointWithGeoCoord(37.600577, 126.864837)// 전자관
    private val MARKER_POINT_STUDENTHALL = MapPoint.mapPointWithGeoCoord(37.600058, 126.864689) // 학관
    private val MARKER_POINT_LIBRARY = MapPoint.mapPointWithGeoCoord(37.597992, 126.864422)// 도서관
    private val MARKER_POINT_VENTURE = MapPoint.mapPointWithGeoCoord(37.597841, 126.865128)// 창업보육센터
    private val MARKER_POINT_MUSEUM = MapPoint.mapPointWithGeoCoord(37.599832, 126.865557)// 항공우주박물관
    private val MARKER_POINT_LECTURE = MapPoint.mapPointWithGeoCoord(37.600067, 126.866707)// 강의동
    private val MARKER_POINT_MAIN = MapPoint.mapPointWithGeoCoord(37.598968, 126.864093)// 본관
    private val MARKER_POINT_ROTC = MapPoint.mapPointWithGeoCoord(37.597752, 126.865713)// 학군단
    private val MARKER_POINT_RESEARCH = MapPoint.mapPointWithGeoCoord(37.597662, 126.864842)// 연구동
    private val MARKER_POINT_RESIDENCE = MapPoint.mapPointWithGeoCoord(37.598137, 126.866190)// 기숙사

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 디버그 키 얻기
        val packageInfo = Utility.getPackageInfo(this, PackageManager.GET_SIGNATURES)
        for (signature in packageInfo.signatures) {
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("asd", Base64.encodeToString(md.digest(), Base64.NO_WRAP))
            } catch (e: NoSuchAlgorithmException) {
//                Log.w(FragmentActivity.TAG, "Unable to get MessageDigest. signature=$signature", e)
            }
        }

        val mapView = MapView(this)
        val mapViewContainer = map_view_main as ViewGroup
        mapViewContainer.addView(mapView)

        // 마커 띄우기
        val marker = MapPOIItem()
        marker.itemName = "과학관"
        marker.tag = 0
        marker.mapPoint = MARKER_POINT_SCIENCE
        marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker)

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
//            Intent(this, PlaceListActivity::class.java).let {
//                it.putExtra("DIVISION_IDX", 2)
//                startActivity(it)
//            }

            Intent(this, IndoorMapActivity::class.java).let {
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
