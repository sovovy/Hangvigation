package com.mobal.hangvigation.ui.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.mobal.hangvigation.ui.indoor_info.PlaceListActivity
import com.mobal.hangvigation.R
import com.mobal.hangvigation.ui.indoor_navi.IndoorMapActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint


class MainActivity : AppCompatActivity() {
    private val MARKER_POINT_SCIENCE = MapPoint.mapPointWithGeoCoord(37.601532, 126.865048) // 과학관
    private val MARKER_POINT_MECHANICAL = MapPoint.mapPointWithGeoCoord(37.601122, 126.864429)// 기계관
    private val MARKER_POINT_ELECTRONIC = MapPoint.mapPointWithGeoCoord(37.600577, 126.864837)// 전자관
    private val MARKER_POINT_STUDENTHALL = MapPoint.mapPointWithGeoCoord(37.600058, 126.864689) // 학관
    private val MARKER_POINT_LIBRARY = MapPoint.mapPointWithGeoCoord(37.597992, 126.864422)// 도서관
    private val MARKER_POINT_VENTURE = MapPoint.mapPointWithGeoCoord(37.597841, 126.865128)// 창업보육센터
    private val MARKER_POINT_MUSEUM = MapPoint.mapPointWithGeoCoord(37.599832, 126.865557)// 항공우주박물관
    private val MARKER_POINT_LECTURE = MapPoint.mapPointWithGeoCoord(37.600067, 126.866707)// 강의동
    private val MARKER_POINT_MAIN = MapPoint.mapPointWithGeoCoord(37.598968, 126.864093)// 본관
    private val MARKER_POINT_ROTC = MapPoint.mapPointWithGeoCoord(37.597793, 126.865940)// 학군단
    private val MARKER_POINT_RESEARCH = MapPoint.mapPointWithGeoCoord(37.597566, 126.864749)// 연구동
    private val MARKER_POINT_RESIDENCE = MapPoint.mapPointWithGeoCoord(37.598137, 126.866190)// 기숙사
    private lateinit var mapView : MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 디버그 키 얻기
//        val packageInfo = Utility.getPackageInfo(this, PackageManager.GET_SIGNATURES)
//        for (signature in packageInfo.signatures) {
//            try {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.d("asd", Base64.encodeToString(md.digest(), Base64.NO_WRAP))
//            } catch (e: NoSuchAlgorithmException) {
////                Log.w(FragmentActivity.TAG, "Unable to get MessageDigest. signature=$signature", e)
//            }
//        }

        mapView = MapView(this)
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.600459, 126.865486), 1, true)
        val mapViewContainer = map_view_main as ViewGroup
        mapViewContainer.addView(mapView)

        // 마커 띄우기
        val marker = MapPOIItem()
        marker.itemName = "안내 시작"
        marker.tag = 1
        marker.mapPoint = MARKER_POINT_SCIENCE
        marker.markerType = MapPOIItem.MarkerType.CustomImage
        marker.customImageResourceId = R.drawable.marker_science
        mapView.addPOIItem(marker)

        val marker2 = MapPOIItem()
        marker2.itemName = "안내 시작"
        marker2.tag = 1
        marker2.mapPoint = MARKER_POINT_MECHANICAL
        marker2.markerType = MapPOIItem.MarkerType.CustomImage
        marker2.customImageResourceId = R.drawable.marker_mechanical
        mapView.addPOIItem(marker2)

        val marker3 = MapPOIItem()
        marker3.itemName = "안내 시작"
        marker3.tag = 1
        marker3.mapPoint = MARKER_POINT_ELECTRONIC
        marker3.markerType = MapPOIItem.MarkerType.CustomImage
        marker3.customImageResourceId = R.drawable.marker_electronic
        mapView.addPOIItem(marker3)

        val marker4 = MapPOIItem()
        marker4.itemName = "안내 시작"
        marker4.tag = 1
        marker4.mapPoint = MARKER_POINT_STUDENTHALL
        marker4.markerType = MapPOIItem.MarkerType.CustomImage
        marker4.customImageResourceId = R.drawable.marker_student
        mapView.addPOIItem(marker4)

        val marker5 = MapPOIItem()
        marker5.itemName = "안내 시작"
        marker5.tag = 1
        marker5.mapPoint = MARKER_POINT_LIBRARY
        marker5.markerType = MapPOIItem.MarkerType.CustomImage
        marker5.customImageResourceId = R.drawable.marker_library
        mapView.addPOIItem(marker5)

        val marker6 = MapPOIItem()
        marker6.itemName = "안내 시작"
        marker6.tag = 1
        marker6.mapPoint = MARKER_POINT_VENTURE
        marker6.markerType = MapPOIItem.MarkerType.CustomImage
        marker6.customImageResourceId = R.drawable.marker_venture
        mapView.addPOIItem(marker6)

        val marker7 = MapPOIItem()
        marker7.itemName = "안내 시작"
        marker7.tag = 1
        marker7.mapPoint = MARKER_POINT_MUSEUM
        marker7.markerType = MapPOIItem.MarkerType.CustomImage
        marker7.customImageResourceId = R.drawable.marker_museum
        mapView.addPOIItem(marker7)

        val marker8 = MapPOIItem()
        marker8.itemName = "안내 시작"
        marker8.tag = 1
        marker8.mapPoint = MARKER_POINT_LECTURE
        marker8.markerType = MapPOIItem.MarkerType.CustomImage
        marker8.customImageResourceId = R.drawable.marker_lecture
        mapView.addPOIItem(marker8)

        val marker9 = MapPOIItem()
        marker9.itemName = "안내 시작"
        marker9.tag = 1
        marker9.mapPoint = MARKER_POINT_MAIN
        marker9.markerType = MapPOIItem.MarkerType.CustomImage
        marker9.customImageResourceId = R.drawable.marker_main
        mapView.addPOIItem(marker9)

        val marker10 = MapPOIItem()
        marker10.itemName = "안내 시작"
        marker10.tag = 1
        marker10.mapPoint = MARKER_POINT_ROTC
        marker10.markerType = MapPOIItem.MarkerType.CustomImage
        marker10.customImageResourceId = R.drawable.marker_rotc
        mapView.addPOIItem(marker10)

        val marker11 = MapPOIItem()
        marker11.itemName = "안내 시작"
        marker11.tag = 1
        marker11.mapPoint = MARKER_POINT_RESEARCH
        marker11.markerType = MapPOIItem.MarkerType.CustomImage
        marker11.customImageResourceId = R.drawable.marker_research
        mapView.addPOIItem(marker11)

        val marker12 = MapPOIItem()
        marker12.itemName = "안내 시작"
        marker12.tag = 1
        marker12.mapPoint = MARKER_POINT_RESIDENCE
        marker12.markerType = MapPOIItem.MarkerType.CustomImage
        marker12.customImageResourceId = R.drawable.marker_residence
        mapView.addPOIItem(marker12)

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
        btn_first_main.setOnClickListener {
//            Intent(this, PlaceListActivity::class.java).let {
//                it.putExtra("DIVISION_IDX", 2)
//                startActivity(it)
//            }

            Intent(this, IndoorMapActivity::class.java).let {
                startActivity(it)
            }
        }
        // 편의시설
        btn_second_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 66)
                startActivity(it)
            }
        }
        // 연구사무실
        btn_third_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 70)
                startActivity(it)
            }
        }
        // 그외
        btn_fourth_main.setOnClickListener {
            Intent(this, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", 86)
                startActivity(it)
            }
        }

//        btn_location_main.setOnClickListener {
//            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading
//        }
    }
}
