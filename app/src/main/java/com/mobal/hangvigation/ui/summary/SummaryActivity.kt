package com.mobal.hangvigation.ui.summary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import com.mobal.hangvigation.R
import kotlinx.android.synthetic.main.activity_summary.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class SummaryActivity : AppCompatActivity(), MapView.POIItemEventListener {
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
            mapView.addPOIItem(marker)
        }
    }

    private fun setSummary(idx: Int) {
        tv_title_summary.text = "${markerName[idx]}까지"

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
