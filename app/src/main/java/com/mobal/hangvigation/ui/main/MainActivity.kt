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
import com.mobal.hangvigation.R


class MainActivity : AppCompatActivity(), MapView.POIItemEventListener {
    // idx별 건물
    // 0: 과학관, 1: 기계관, 2: 전자관, 3: 학관, 4: 도서관, 5: 창업보육센터 6: 항공우주박물관
    // 7: 강의동, 8: 본관, 9: 학군단, 10: 연구동, 11: 기숙사
    private val markerPoints: Array<MapPoint> = arrayOf(
        MapPoint.mapPointWithGeoCoord(37.601532, 126.865048), MapPoint.mapPointWithGeoCoord(37.601122, 126.864429),
        MapPoint.mapPointWithGeoCoord(37.600577, 126.864837), MapPoint.mapPointWithGeoCoord(37.600058, 126.864689),
        MapPoint.mapPointWithGeoCoord(37.597992, 126.864422), MapPoint.mapPointWithGeoCoord(37.597841, 126.865128),
        MapPoint.mapPointWithGeoCoord(37.599832, 126.865557), MapPoint.mapPointWithGeoCoord(37.600067, 126.866707),
        MapPoint.mapPointWithGeoCoord(37.598968, 126.864093), MapPoint.mapPointWithGeoCoord(37.597793, 126.865940),
        MapPoint.mapPointWithGeoCoord(37.597566, 126.864749), MapPoint.mapPointWithGeoCoord(37.598137, 126.866190)
    )
    private val markerName = arrayOf("과학관", "기계관", "전자관", "학관", "도서관", "창업보육센터", "항공우주박물관", "강의동", "본관", "학군단", "연구동", "기숙사")
    private lateinit var mapView : MapView
    private var permissions = arrayOf(ACCESS_FINE_LOCATION)
    private var mapCode = 0
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

        // 맵이 필요한 4가지 경우
        // 1. 메인, 2. summary, 3. outnavi, 4. indoor info
        // 1,2를 제외하고 나머지 경우에서 여기로 넘어올 때 putExtra로 "MAP_CODE"를 넘겨준다 (변수 mapCode로도 값을 갖고 있는다)
        // "MAP_CODE"는 2: summary, 3: outnavi, 4: indoor info

        mapCode = intent.getIntExtra("MAP_CODE", 1)

        when(mapCode){
            // Main Map
            1 -> {
                // 경도 위도를 지정해 맵 띄우기
                floatingMap(37.600459, 126.865486)
                // idx 로 건물을 지정해 마커 띄우기
                floatingMarker(arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), "안내 시작")
                // main 관련 listener 부착
                setMainListener()
                activeTrackingMode()
            }
            // Outnavi Map
            3 -> {
                floatingMap(37.600459, 126.865486)
            }
            // IndoorInfo Map
            4 -> {
                floatingMap(37.600459, 126.865486)
            }
        }

    }
    private fun activeTrackingMode() {
        mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.indoor_location,MapPOIItem.ImageOffset(30,50))
        mapView.setCustomCurrentLocationMarkerImage(R.drawable.indoor_location,MapPOIItem.ImageOffset(30,50))
        btn_location_main.setOnClickListener {
            if (flag) {
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
            } else {
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            }
            flag = !flag
        }
    }

    private fun floatingMap(lat: Double, long: Double) {
        mapView = MapView(this)
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, long), 1, true)
        val mapViewContainer = map_view_main as ViewGroup
        mapViewContainer.addView(mapView)

        btn_school_main.setOnClickListener {
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.600459, 126.865486), 1, true)
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
            mapView.addPOIItem(marker)
        }
    }

    private fun setMainListener() {
        // search bar enter key event
        et_bar_search.setOnKeyListener { _, keyCode, event ->
            if ((event.action== KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                moveToSearch(et_bar_search.text.toString())
                true
            } else {
                false
            }
        }

        // 검색 버튼 관련 VISIBILITY 관리
        iv_search_main.setOnClickListener {
            cl_second_main.visibility = View.GONE
            cl_search_main.visibility = View.VISIBLE
        }

        iv_cancel_search.setOnClickListener {
            cl_second_main.visibility = View.VISIBLE
            cl_search_main.visibility = View.GONE
        }

        // 검색어 공통 listener
        val searchListener = View.OnClickListener {
            moveToSearch((it as TextView).text.toString())
        }

        tv_hot1_search.setOnClickListener(searchListener)
        tv_hot2_search.setOnClickListener(searchListener)
        tv_hot3_search.setOnClickListener(searchListener)
        tv_hot4_search.setOnClickListener(searchListener)
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

//            Intent(this, IndoorMapActivity::class.java).let {
//                startActivity(it)
//            }
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
        when(mapCode) {
            // main일 땐 summary 화면으로 변환
            1 -> {
                cl_second_main.visibility = View.GONE
                cl_search_main.visibility = View.GONE
                cl_summary_main.visibility = View.VISIBLE
                tv_title_summary.text = "${markerName[marker!!.tag]}까지"
                mapView.setMapCenterPointAndZoomLevel(markerPoints[marker.tag], 1, true)
                setSummaryListener() // 안내 시작 버튼 클릭
                mapView.removeAllPOIItems() // 모든 마커 지우기
                floatingMarker(arrayOf(marker.tag), "")
//                mapView.fitMapViewAreaToShowAllPOIItems() // 모든 마커를 보여줌

                /* TODO
                 * 소요시간, 도보로 이동할 거리 띄우기
                 * Tmap에서 경로 정보만 받아와서
                 * 맵 위에 polyline 그리기
                 */
            }
            // summary 일 땐 실내로 들어가는 경우에만 indoor map 으로 변환
            2 -> {
                // TODO indoor map 으로 변환
            }
        }
        // outnavi, indoor info일 땐 do nothing (일단
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {
    }
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }

    private fun setSummaryListener() {
        // 안내 시작 버튼 관리
        btn_start_summary.setOnClickListener {
            /* TODO
             * 실외 네비
             */
            mapCode = 3
        }
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
/* TODO
     * 마커들 클래스로 묶을 수 있으면 묶기
     * 현재 위치 버튼 -> 화면이 고정될 수 있도록
     * (참고) api 메서드 사용: https://github.com/sesna99/BusComplain2/blob/d85ab8fff00b1f8b0c34d92b39cb85705f8ab3cc/app/src/main/java/trycatch/dev/buscomplain/View/Activity/SearchDetailActivity.kt
*/