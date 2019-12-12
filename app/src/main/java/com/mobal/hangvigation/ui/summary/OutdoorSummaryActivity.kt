package com.mobal.hangvigation.ui.summary

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.mobal.hangvigation.R
import com.mobal.hangvigation.model.*
import com.mobal.hangvigation.network.ApplicationController
import com.mobal.hangvigation.network.NetworkService
import com.mobal.hangvigation.ui.outdoor_navi.OutdoorNaviActivity
import kotlinx.android.synthetic.main.activity_summary.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPointBounds

class OutdoorSummaryActivity : AppCompatActivity(), MapView.POIItemEventListener, MapView.CurrentLocationEventListener {
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
    private val markerImgs: Array<Int> = arrayOf(
        R.drawable.marker_science,
        R.drawable.marker_mechanical,
        R.drawable.marker_electronic,
        R.drawable.marker_student,
        R.drawable.marker_library,
        R.drawable.marker_venture,
        R.drawable.marker_museum,
        R.drawable.marker_lecture,
        R.drawable.marker_main,
        R.drawable.marker_rotc,
        R.drawable.marker_research,
        R.drawable.marker_residence
    )
    private lateinit var mapViewContainer : ViewGroup
    private lateinit var mapView : MapView
    private var markerIdx : Int = 0
    private var markerStart : Int = 0
    private var markerDest : Int = 0
    private lateinit var networkService : NetworkService
    private var currentPoint : MapPoint? = null
    private var pointLatitude = arrayListOf<Double>()
    private var destinationX : Int = 0
    private var destinationY : Int = 0
    private var destinationZ : Int = 0
    private var mRoute = ArrayList<PostRouteResponseData>() // (건물1층 -> 실내목적지) 경로

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        networkService = ApplicationController.instance.networkService


        /* IndoorSummary에서 받은 intent 관리 */
        // 실내->실외
        if(intent.hasExtra("BUILDING_START")) {
            markerStart = intent.getIntExtra("BUILDING_START", 0)
            markerDest = intent.getIntExtra("BUILDING_DEST", 0)
            markerIdx = intent.getIntExtra("BUILDING_DEST", 0)
        }
        // 실외->실내
        else if(intent.hasExtra("BUILDING")){
            markerIdx = intent.getIntExtra("BUILDING", 0)
        }
        // Main에서 받은 intent
        else {
            markerIdx = intent.getIntExtra("MARKER_IDX", 0)
        }
        destinationX = intent.getIntExtra("X", 0)
        destinationY = intent.getIntExtra("Y", 0)
        destinationZ = intent.getIntExtra("Z", 0)
        if(intent.hasExtra("ROUTE")) {
            mRoute = intent.getParcelableArrayListExtra("ROUTE")
        }
    }

    override fun onResume() {
        super.onResume()

        floatingMap(markerPoints[markerIdx].mapPointGeoCoord.latitude, markerPoints[markerIdx].mapPointGeoCoord.longitude)

        if(intent.hasExtra("BUILDING_START")) {
            floatingMarker(markerStart, markerName[markerStart])
            floatingMarker(markerDest, markerName[markerDest])
        } else {
            floatingMarker(markerIdx, markerName[markerIdx])
        }
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
        mapView.setCurrentLocationEventListener(this)
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    private fun floatingMarker(idx: Int, msg: String) {

        mapView.setPOIItemEventListener(this)

        val marker = MapPOIItem()
        marker.isShowCalloutBalloonOnTouch = false
        marker.itemName = msg
        marker.mapPoint = markerPoints[idx]
        marker.markerType = MapPOIItem.MarkerType.CustomImage
        marker.customImageResourceId = markerImgs[idx]
        marker.setCustomImageAnchor(0.5f, 0.5f)
        mapView.addPOIItem(marker)
    }

    private fun setSummary(idx: Int) {
        rl_floor_summary.visibility = View.GONE
        sv_horizon_summary.visibility = View.GONE
        tv_title_summary.text = markerName[idx]

        // 현재 위치가 잡히면 통신
        if(currentPoint != null) {
            pointLatitude.clear()
            networkOutdoorRoute(currentPoint!!.mapPointGeoCoord.latitude, currentPoint!!.mapPointGeoCoord.longitude)
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        }


        // 안내 시작 버튼
        btn_start_summary.setOnClickListener {
            Intent(this, OutdoorNaviActivity::class.java).let {
                it.putExtra("MARKER_IDX", markerIdx)
                if(mRoute != null) {
                    it.putExtra("ROUTE", mRoute)
                }
                startActivity(it)
                finish()
            }
        }
    }

    /* 통신 */
    private fun networkOutdoorRoute(x: Double, y: Double) {
        var postOutdoor = networkService.postOutdoor(
            PostOutdoorData(y, x,
                markerPoints[markerIdx].mapPointGeoCoord.longitude,
                markerPoints[markerIdx].mapPointGeoCoord.latitude
            )
        )
        if (intent.hasExtra("BUILDING_DEST")) {
            postOutdoor = networkService.postOutdoor(
                PostOutdoorData(y, x,
                    markerPoints[intent.getIntExtra("BUILDING_DEST", 0)].mapPointGeoCoord.longitude,
                    markerPoints[intent.getIntExtra("BUILDING_DEST", 0)].mapPointGeoCoord.latitude
                    )
            )
        }

        postOutdoor.enqueue(object : Callback<PostOutdoorResponse> {
            override fun onFailure(call: Call<PostOutdoorResponse>?, t: Throwable?) {
                Log.d("OUTDOOR_FAIL", t.toString())
            }

            override fun onResponse(call: Call<PostOutdoorResponse>?, response: Response<PostOutdoorResponse>?) {
                if(response!!.isSuccessful){
                    val data = response.body().data
                    var totalTime = data[data.size - 1].y
                    var totalDistance = data[data.size - 1].x

                    // 소요시간, 거리 띄우기
                    tv_time_summary.text = "${totalTime.toInt()}분"
                    tv_distance_summary.text = "${totalDistance.toInt()}m"

                    // 경로 그리기
                    drawRoute(data)
                } else{
                    Log.d("OUTDOOR_UNSUCCESSFUL", response.message())
                }
            }
        })
    }

    private fun drawRoute(data: ArrayList<PostOutdoorResponseData>) {

        var polyline = MapPolyline()
        polyline.lineColor = Color.parseColor("#ff6a6a")

        data.forEach {
            // 좌표가 겹치는게 있어서 걸러줘야함
            if(!pointLatitude.contains(it.y) && it.type != "Properties") {
                pointLatitude.add(it.y)
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(it.y, it.x))
            }
        }

        mapView.addPolyline(polyline)
        // polyline 전체가 지도에 다 나오게 하기
        val mapPointBounds = MapPointBounds(polyline.mapPoints)
        val padding = 100 // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding))
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

        // outnavi, indoor info일 땐 do nothing
        if(destinationX == 0) {
            return
        }

        // 마커 터치하면 IndoorSummary로 이동
        Intent(this, IndoorSummaryActivity::class.java).let {
            it.putExtra("X", destinationX)
            it.putExtra("Y", destinationY)
            it.putExtra("Z", destinationZ)
            it.putExtra("ROUTE", mRoute)
            startActivity(it)
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?,p1: MapPOIItem?,p2: MapPOIItem.CalloutBalloonButtonType?) {
    }
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }

    /* 현재 위치 관련 메서드들 */
    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {
        currentPoint = p1!!
        setSummary(markerIdx)
    }
    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
        Log.d("current", "cancel")
    }
    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
    }
    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        Log.d("current", "fail")
    }
}
