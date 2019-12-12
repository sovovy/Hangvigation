package com.mobal.hangvigation.ui.main

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mobal.hangvigation.ui.indoor_info.PlaceListActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MotionEvent
import com.mobal.hangvigation.R
import com.mobal.hangvigation.ui.summary.OutdoorSummaryActivity


class MainActivity : AppCompatActivity(), MapView.POIItemEventListener {
    // idx별 건물
    // 0: 과학관, 1: 기계관, 2: 전자관, 3: 학관, 4: 도서관, 5: 창업보육센터 6: 항공우주박물관
    // 7: 강의동, 8: 본관, 9: 학군단, 10: 연구동, 11: 기숙사
    private val markerPoints: Array<MapPoint> = arrayOf(
        MapPoint.mapPointWithGeoCoord(37.60161074286123, 126.86512165734828), MapPoint.mapPointWithGeoCoord(37.601213581669576, 126.86448821597422),
        MapPoint.mapPointWithGeoCoord(37.60061718974406, 126.8649366022691), MapPoint.mapPointWithGeoCoord(37.60006729180758, 126.86467997345024),
        MapPoint.mapPointWithGeoCoord(37.598143454113874, 126.8644852913131), MapPoint.mapPointWithGeoCoord(37.59793706630939, 126.86521887909963),
        MapPoint.mapPointWithGeoCoord(37.599712420491066, 126.8655723834837), MapPoint.mapPointWithGeoCoord(37.600182036444735, 126.86654258017211),
        MapPoint.mapPointWithGeoCoord(37.598999067816706, 126.86420064147649), MapPoint.mapPointWithGeoCoord(37.59786575148311, 126.86588993989628),
        MapPoint.mapPointWithGeoCoord(37.59760323103674, 126.86480899679448), MapPoint.mapPointWithGeoCoord(37.59816840394317, 126.866614119594)
    )
    private lateinit var mapViewContainer : ViewGroup
    private lateinit var mapView : MapView
    private var permissions = arrayOf(ACCESS_FINE_LOCATION)
    private var flag = false

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        // 경도 위도를 지정해 맵 띄우기
        floatingMap(37.599759, 126.865486)
        // idx 로 건물을 지정해 마커 띄우기
        floatingMarker(arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), "안내 시작")
        // main 관련 listener 부착
        setListener()
        activeTrackingMode()
    }

    override fun onPause() {
        super.onPause()
        mapViewContainer.removeView(mapView)
    }

    override fun onBackPressed() {
        if (iv_cancel_search.visibility == View.VISIBLE)
            iv_cancel_search.performClick()
        else
            super.onBackPressed()
    }

    private fun activeTrackingMode() {
        mapView.setShowCurrentLocationMarker(true)
        mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.mlocation_circle,MapPOIItem.ImageOffset(32,32))
        mapView.setCustomCurrentLocationMarkerImage(R.drawable.mlocation_circle,MapPOIItem.ImageOffset(32,32))

        btn_location_main.setOnClickListener {
            if (flag) {
                btn_location_main.background = ContextCompat.getDrawable(this, R.drawable.mlocation)
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
            } else {
                btn_location_main.background = ContextCompat.getDrawable(this, R.drawable.mlocation_tracking)
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            }
            flag = !flag
        }
    }

    private fun floatingMap(lat: Double, long: Double) {
        mapView = MapView(this)
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, long), 1, true)
        mapViewContainer = map_view_main as ViewGroup
        mapViewContainer.addView(mapView)

        btn_school_main.setOnClickListener {
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.599759, 126.865486), 1, true)
        }
    }

    private fun floatingMarker(idxArr: Array<Int>, msg: String) {
        val markerImgs: Array<Int> = arrayOf(R.drawable.marker_science, R.drawable.marker_mechanical, R.drawable.marker_electronic, R.drawable.marker_student,
            R.drawable.marker_library, R.drawable.marker_venture, R.drawable.marker_museum, R.drawable.marker_lecture, R.drawable.marker_main, R.drawable.marker_rotc,
            R.drawable.marker_research, R.drawable.marker_residence)

        mapView.setPOIItemEventListener(this)

        for (i in idxArr) {
            val marker = MapPOIItem()
            marker.itemName = msg
            marker.tag = i
            marker.mapPoint = markerPoints[i]
            marker.markerType = MapPOIItem.MarkerType.CustomImage
            marker.customImageResourceId = markerImgs[i]
            marker.setCustomImageAnchor(0.5f, 0.5f)
            mapView.addPOIItem(marker)
        }
    }

    private fun setListener() {
        // search bar enter key event
        et_bar_search.setOnKeyListener { _, keyCode, event ->
            if ((event.action== KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                moveToSearch(et_bar_search.text.toString())
                true
            } else {
                false
            }
        }

        et_bar_search.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                    cl_second_main.visibility = View.GONE
                    cl_search_main.visibility = View.VISIBLE
                    iv_search_main.visibility = View.GONE
                    iv_cancel_search.visibility = View.VISIBLE
            }
            false
        }

        // 검색 버튼 관련 VISIBILITY 관리
        iv_search_main.setOnClickListener {
            cl_second_main.visibility = View.GONE
            cl_search_main.visibility = View.VISIBLE
            iv_search_main.visibility = View.GONE
            iv_cancel_search.visibility = View.VISIBLE
        }

        iv_cancel_search.setOnClickListener {
            cl_second_main.visibility = View.VISIBLE
            cl_search_main.visibility = View.GONE
            iv_search_main.visibility = View.VISIBLE
            iv_cancel_search.visibility = View.GONE
        }

        // 검색어 공통 listener
        val searchListener = View.OnClickListener {
            moveToSearch((it as TextView).text.toString())
        }

        tv_hot1_search.setOnClickListener(searchListener)
        tv_hot2_search.setOnClickListener(searchListener)
        tv_hot3_search.setOnClickListener(searchListener)
        tv_hot4_search.setOnClickListener(searchListener)
        tv_hot5_search.setOnClickListener(searchListener)
        tv_recent1_search.setOnClickListener(searchListener)
        tv_recent2_search.setOnClickListener(searchListener)
        tv_recent3_search.setOnClickListener(searchListener)
        tv_recent4_search.setOnClickListener(searchListener)

        // 실내 장소 카테고리 버튼 관리
        // 강의실
        btn_first_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 2)
                it.putExtra("TITLE", "강의실")
                startActivity(it)
            }
        }
        // 편의시설
        btn_second_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 66)
                it.putExtra("TITLE", "편의시설")
                startActivity(it)
            }
        }
        // 연구사무실
        btn_third_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 70)
                it.putExtra("TITLE", "연구∙사무실")
                startActivity(it)
            }
        }
        // 그외
        btn_fourth_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 86)
                it.putExtra("TITLE", "그 외")
                startActivity(it)
            }
        }
    }

    private fun moveToSearch(word: String) {
        Intent(this@MainActivity, PlaceListActivity::class.java).let {
            it.putExtra("QUERY", word)
            startActivity(it)
        }

    }

    /* 마커 터치 관련 메서드들 */
    override fun onCalloutBalloonOfPOIItemTouched(map: MapView?, marker: MapPOIItem?) {
        Intent(this, OutdoorSummaryActivity::class.java).let {
            it.putExtra("MARKER_IDX", marker!!.tag)
            startActivity(it)
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {
    }
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }

    /* Location permission 을 위한 메서드들 */
    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded = ArrayList<String>()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this@MainActivity, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ACCESS_FINE_LOCATION
            )
            return false
        }
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted")
                }
            }
        }
    }
    companion object {
        /* Location permission 을 위한 필드 */
        const val REQUEST_ACCESS_FINE_LOCATION = 10 // code you want.
    }
}