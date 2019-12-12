package com.mobal.hangvigation.ui.outdoor_navi

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mobal.hangvigation.R
import com.mobal.hangvigation.model.*
import com.mobal.hangvigation.network.ApplicationController
import com.mobal.hangvigation.network.NetworkService
import com.mobal.hangvigation.ui.indoor_navi.IndoorNaviActivity
import kotlinx.android.synthetic.main.activity_outdoor_navi.*
import net.daum.mf.map.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.abs

class OutdoorNaviActivity : AppCompatActivity(), MapView.POIItemEventListener, MapView.CurrentLocationEventListener {
    // idx별 건물
    // 0: 과학관, 1: 기계관, 2: 전자관, 3: 학관, 4: 도서관, 5: 창업보육센터
    // 6: 항공우주박물관, 7: 강의동, 8: 본관, 9: 학군단, 10: 연구동, 11: 기숙사
    private val markerPoints: Array<MapPoint> = arrayOf(
        MapPoint.mapPointWithGeoCoord(37.60161074286123, 126.86512165734828),
        MapPoint.mapPointWithGeoCoord(37.601213581669576, 126.86448821597422),
        MapPoint.mapPointWithGeoCoord(37.60061718974406, 126.8649366022691),
        MapPoint.mapPointWithGeoCoord(37.60006729180758, 126.86467997345024),
        MapPoint.mapPointWithGeoCoord(37.598143454113874, 126.8644852913131),
        MapPoint.mapPointWithGeoCoord(37.59793706630939, 126.86521887909963),
        MapPoint.mapPointWithGeoCoord(37.599712420491066, 126.8655723834837),
        MapPoint.mapPointWithGeoCoord(37.600182036444735, 126.86654258017211),
        MapPoint.mapPointWithGeoCoord(37.598999067816706, 126.86420064147649),
        MapPoint.mapPointWithGeoCoord(37.59786575148311, 126.86588993989628),
        MapPoint.mapPointWithGeoCoord(37.59760323103674, 126.86480899679448),
        MapPoint.mapPointWithGeoCoord(37.59816840394317, 126.866614119594)
    )
    private val markerName =
        arrayOf("과학관", "기계관", "전자관", "학생회관", "도서관", "창업보육센터", "항공우주박물관", "강의동", "본관", "학군단", "연구동", "기숙사")
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
    private lateinit var mapViewContainer: ViewGroup
    private lateinit var mapView: MapView
    private var markerIdx: Int = 0
    private lateinit var networkService: NetworkService
    private var currentPoint: MapPoint? = null
    private var currentLat: Double = 0.0
    private var currentLon: Double = 0.0
    private var pointLat = arrayListOf<Double>()
    private var mRoute = ArrayList<PostRouteResponseData>() // (건물1층 -> 실내목적지) 경로

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outdoor_navi)

        networkService = ApplicationController.instance.networkService

        markerIdx = intent.getIntExtra("MARKER_IDX", 0)
        if(intent.hasExtra("ROUTE")) {
            mRoute = intent.getParcelableArrayListExtra("ROUTE")
        }
    }

    override fun onResume() {
        super.onResume()

        floatingMap(
            markerPoints[markerIdx].mapPointGeoCoord.latitude,
            markerPoints[markerIdx].mapPointGeoCoord.longitude
        )
        floatingMarker(markerIdx, markerName[markerIdx])

        btn_guideEnd_outdoor.setOnClickListener {
            Toast.makeText(this@OutdoorNaviActivity, "안내를 종료합니다.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.removeAllPolylines()
        mapViewContainer.removeView(mapView)
    }

    private fun floatingMap(lat: Double, long: Double) {
        mapView = MapView(this)
        if(currentPoint != null) {
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(currentLat, currentLon), 0, true)
        } else {
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, long), 0, true)
        }
        mapViewContainer = map_view_outdoor as ViewGroup
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

    private fun drawRoute(data: ArrayList<PostOutdoorResponseData>) {

        var polyline = MapPolyline()
        polyline.lineColor = Color.parseColor("#ff6a6a")

        polyline.addPoint(currentPoint)
        data.forEach {
            if(!pointLat.contains(it.y) && it.type != "Properties") {
                pointLat.add(it.y)
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(it.y, it.x))
            }
        }
        polyline.addPoint(markerPoints[markerIdx])
        mapView.addPolyline(polyline)
    }

    /* 현재 위치 관련 메서드들 */
    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {
        currentPoint = p1!!
        currentLat = currentPoint!!.mapPointGeoCoord.latitude
        currentLon = currentPoint!!.mapPointGeoCoord.longitude
        Log.d("currentPoint", "[$currentLat,  $currentLon]")

        if(mapView.poiItems.isNotEmpty()) {
            mapView.moveCamera(CameraUpdateFactory.newMapPoint(currentPoint, (-2).toFloat()))
            pointLat.clear()
            mapView.removeAllPolylines()

            // 5초간 멈춘다음에 다시 그리기
            val handler = Handler()
            handler.postDelayed({
                networkOutdoorRoute(currentLat, currentLon)
            }, 1000)
        }
        else {
            networkOutdoorRoute(currentLat, currentLon)
        }


        setListener()
    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
    }
    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
    }
    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
    }

    private fun setListener() {
        /* 현재 위치가 목적지랑 가까운 경우 */
        if(abs(currentLat - markerPoints[markerIdx].mapPointGeoCoord.latitude) <= 0.0002) {
            // IndoorSummary에서 Intent가 있는 경우 -> IndoorNavi로 이동
            if(intent.hasExtra("ROUTE")) {
                btn_guideEnd_outdoor.visibility = View.VISIBLE
                btn_guideEnd_outdoor.text = "실내 길안내 시작"
                btn_guideEnd_outdoor.setOnClickListener {
                    Intent(this, IndoorNaviActivity::class.java).let {
                        it.putExtra("ROUTE", mRoute)
                        var floorArr = arrayListOf<Int>()
                        mRoute.forEach {d ->
                            if (!floorArr.contains(d.z)) {
                                floorArr.add(d.z)
                            }
                        }
                        it.putExtra("FLOOR_ARR", floorArr)
                        startActivity(it)
                        finish()
                    }
                }
            }
            // just 실외길찾기에서 끝나는 경우
            else {
                btn_guideEnd_outdoor.visibility = View.VISIBLE
                btn_guideEnd_outdoor.text = "안내 종료"
                btn_guideEnd_outdoor.setOnClickListener {
                    finish()
                }
            }
        }
    }

    /* 통신 */
    private fun networkOutdoorRoute(x: Double, y: Double) {
        val postOutdoor = networkService.postOutdoor(
            PostOutdoorData(y, x,
                markerPoints[markerIdx].mapPointGeoCoord.longitude,
                markerPoints[markerIdx].mapPointGeoCoord.latitude
            )
        )

        postOutdoor.enqueue(object : Callback<PostOutdoorResponse> {
            override fun onFailure(call: Call<PostOutdoorResponse>?, t: Throwable?) {
                Log.d("OUTDOOR_FAIL", t.toString())
            }

            override fun onResponse(call: Call<PostOutdoorResponse>?, response: Response<PostOutdoorResponse>?) {
                if(response!!.isSuccessful){
                    val data = response.body().data

                    // 경로 그리기
                    drawRoute(data)

                } else{
                    Log.d("OUTDOOR_UNSUCCESSFUL", response.message())
                }
            }
        })
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
        // summary 일 땐 실내로 들어가는 경우에만 indoor map 으로 변환

        // TODO indoor map 으로 변환

        // outnavi, indoor info일 땐 do nothing (일단
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {}
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }
}