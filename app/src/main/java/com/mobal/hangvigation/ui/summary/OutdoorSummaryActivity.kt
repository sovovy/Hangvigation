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

class OutdoorSummaryActivity : AppCompatActivity(), MapView.POIItemEventListener {
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
    private val markerName = arrayOf("과학관", "기계관", "전자관", "학생회관", "도서관", "창업보육센터", "항공우주박물관", "강의동", "본관", "학군단", "연구동", "기숙사")
    private lateinit var mapViewContainer : ViewGroup
    private lateinit var mapView : MapView
    private var markerIdx : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        markerIdx = intent.getIntExtra("MARKER_IDX", 0)
    }

    override fun onResume() {
        super.onResume()

        floatingMap(markerPoints[markerIdx].mapPointGeoCoord.latitude, markerPoints[markerIdx].mapPointGeoCoord.longitude)
        floatingMarker(arrayOf(markerIdx), markerName[markerIdx])
        setSummary(markerIdx)
    }

    override fun onPause() {
        super.onPause()
        mapViewContainer.removeView(mapView)
    }

    private fun floatingMap(lat: Double, long: Double) {
        mapView = MapView(this)
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, long), 1, true)
        mapViewContainer = map_view_summary as ViewGroup
        mapViewContainer.addView(mapView)
    }

    private fun floatingMarker(idxArr: Array<Int>, msg: String) {
        val markerImgs: Array<Int> = arrayOf(R.drawable.marker_science, R.drawable.marker_mechanical, R.drawable.marker_electronic, R.drawable.marker_student,
            R.drawable.marker_library, R.drawable.marker_venture, R.drawable.marker_museum, R.drawable.marker_lecture, R.drawable.marker_main, R.drawable.marker_rotc,
            R.drawable.marker_research, R.drawable.marker_residence)

        mapView.setPOIItemEventListener(this)

        for (i in idxArr) {
            val marker = MapPOIItem()
            marker.isShowCalloutBalloonOnTouch = false
            marker.itemName = msg
            marker.tag = i
            marker.mapPoint = markerPoints[i]
            marker.markerType = MapPOIItem.MarkerType.CustomImage
            marker.customImageResourceId = markerImgs[i]
            marker.setCustomImageAnchor(0.5f, 0.5f)
            mapView.addPOIItem(marker)
        }
    }

    private fun setSummary(idx: Int) {
        rl_floor_summary.visibility = View.GONE
        sv_horizon_summary.visibility = View.GONE
        tv_title_summary.text = markerName[idx]

        /* TODO
         * 소요시간, 도보로 이동할 거리 띄우기
         * Tmap에서 경로 정보만 받아와서
         * 맵 위에 polyline 그리기
         */
        // 안내 시작 버튼
//        btn_start_summary.setOnClickListener {
//            Intent(this, OutNavi::class.java).let {
//                startActivity(it)
//            }
//        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
        // summary 일 땐 실내로 들어가는 경우에만 indoor map 으로 변환

        // TODO indoor map 으로 변환

        // outnavi, indoor info일 땐 do nothing (일단
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?,p1: MapPOIItem?,p2: MapPOIItem.CalloutBalloonButtonType?) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }
}
