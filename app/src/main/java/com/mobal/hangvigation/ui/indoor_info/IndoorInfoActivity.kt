package com.mobal.hangvigation.ui.indoor_info

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.mobal.hangvigation.R
import com.mobal.hangvigation.model.GetInfoResponse
import com.mobal.hangvigation.model.GetInfoResponseData
import com.mobal.hangvigation.network.ApplicationController
import com.mobal.hangvigation.network.NetworkService
import com.mobal.hangvigation.ui.indoor_navi.IndoorNaviActivity
import kotlinx.android.synthetic.main.activity_indoor_info.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IndoorInfoActivity : AppCompatActivity() {
    // idx별 건물
    // 0: 과학관, 1: 기계관, 2: 전자관, 3: 학관, 4: 도서관, 5: 창업보육센터 6: 항공우주박물관
    // 7: 강의동, 8: 본관, 9: 학군단, 10: 연구동, 11: 기숙사
    private var networkService : NetworkService = ApplicationController.instance.networkService
    private lateinit var mapViewContainer : ViewGroup
    private lateinit var mapView : MapView
    private var placeIdx : Int = 1
    private var buildingIdx = hashMapOf(
        Pair("과학관",0), Pair("기계관",1), Pair("전자관",2), Pair("학관",3),
        Pair("도서관",4), Pair("창업보육센터",5), Pair("항공우주박물관",6), Pair("강의동",7),
        Pair("본관",8), Pair("학군단",9), Pair("연구동",10), Pair("기숙사",11)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_indoor_info)

        placeIdx = intent.getIntExtra("INDOOR_PLACE_IDX", 1)
    }

    override fun onResume() {
        super.onResume()
        communication()
    }

    override fun onPause() {
        super.onPause()
        mapViewContainer.removeView(mapView)
    }

    private fun setMapView(name: String, lat: Double, long: Double) {

        mapView = MapView(this)
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, long), 1, true)
        mapViewContainer = map_view_info as ViewGroup
        mapViewContainer.addView(mapView)

        mapView.removeAllPOIItems()
        val marker = MapPOIItem()
        marker.itemName = name
        marker.tag = 1
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(lat, long)
        marker.markerType = MapPOIItem.MarkerType.CustomImage
        marker.customImageResourceId = R.drawable.indoor_location
        marker.setCustomImageAnchor(0.5f, -1f)
        mapView.addPOIItem(marker)
    }

    private fun communication() {
        val getInfo = networkService.getInfo(placeIdx)
        getInfo.enqueue(object: Callback<GetInfoResponse>{
            override fun onFailure(call: Call<GetInfoResponse>?, t: Throwable?) {
                Log.d("Error::Info", "$t")
            }

            override fun onResponse(call: Call<GetInfoResponse>?, response: Response<GetInfoResponse>?) {
                if (response!!.isSuccessful){
                    setInfoUI(response.body().data)
                    val markerPoints: Array<MapPoint> = arrayOf(
                        MapPoint.mapPointWithGeoCoord(37.60161074286123, 126.86512165734828), MapPoint.mapPointWithGeoCoord(37.601213581669576, 126.86448821597422),
                        MapPoint.mapPointWithGeoCoord(37.60061718974406, 126.8649366022691), MapPoint.mapPointWithGeoCoord(37.60006729180758, 126.86467997345024),
                        MapPoint.mapPointWithGeoCoord(37.598143454113874, 126.8644852913131), MapPoint.mapPointWithGeoCoord(37.59793706630939, 126.86521887909963),
                        MapPoint.mapPointWithGeoCoord(37.599712420491066, 126.8655723834837), MapPoint.mapPointWithGeoCoord(37.600182036444735, 126.86654258017211),
                        MapPoint.mapPointWithGeoCoord(37.598999067816706, 126.86420064147649), MapPoint.mapPointWithGeoCoord(37.59786575148311, 126.86588993989628),
                        MapPoint.mapPointWithGeoCoord(37.59760323103674, 126.86480899679448), MapPoint.mapPointWithGeoCoord(37.59816840394317, 126.866614119594)
                    )
                    val tmpName = response.body().data.building?:""
                    val tmpPoint = markerPoints[buildingIdx[tmpName] ?:0].mapPointGeoCoord
                    setMapView(tmpName, tmpPoint.latitude, tmpPoint.longitude)
                }
            }

        })
    }

    private fun setInfoUI(data: GetInfoResponseData) {
        tv_name_indoor_info.text = data.name?: "${data.building} ${data.num}호"

        if (data.name!=null && data.num!=null) {
            tv_position_indoor_info.text = "${data.building} ${data.floor} ${data.num}호"
        } else {
            tv_position_indoor_info.text = "${data.building} ${data.floor}"
        }

        if (data.tag1!=null) {
            tv_tag1_indoor_info.text = data.tag1
            tv_tag1_indoor_info.visibility = View.VISIBLE
        }
        if (data.tag2!=null) {
            tv_tag2_indoor_info.text = data.tag2
            tv_tag2_indoor_info.visibility = View.VISIBLE
        }
        if (data.tag3!=null) {
            tv_tag3_indoor_info.text = data.tag3
            tv_tag3_indoor_info.visibility = View.VISIBLE
        }
        tv_detail_info.text = data.info?:"상세 정보가 없습니다."

        btn_guide_info.setOnClickListener {
            // TODO intent indoor navi
            Intent(this, IndoorNaviActivity::class.java).let {
                startActivity(it)
            }
        }
    }
}
