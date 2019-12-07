package com.mobal.hangvigation.ui.summary

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.mobal.hangvigation.R
import com.mobal.hangvigation.model.*
import com.mobal.hangvigation.network.ApplicationController
import com.mobal.hangvigation.network.NetworkService
import com.mobal.hangvigation.ui.indoor_navi.AccessPoint
import com.mobal.hangvigation.ui.indoor_navi.IndoorNaviActivity
import com.mobal.hangvigation.ui.indoor_navi.InnerMapView
import kotlinx.android.synthetic.main.activity_summary.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IndoorSummaryActivity : AppCompatActivity(){
    private var accessPoints: ArrayList<AccessPoint> = ArrayList()
    private lateinit var networkService: NetworkService
    private var wifiManager: WifiManager? = null
    private lateinit var scanResult: List<ScanResult>
    private var permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
    lateinit var mapView : InnerMapView
    private var lastFloor = 0
    val mRoute = HashMap<Int, FloatArray>()

    private val mWifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null) {
                if (action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    getWIFIScanResult()
                    wifiManager!!.startScan()
                } else if (action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                    context.sendBroadcast(Intent("wifi.ON_NETWORK_STATE_CHANGED"))
                }

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        networkService = ApplicationController.instance.networkService

        init()

    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                finish()
            }
        }
        initWIFIScan()
    }

    private fun init() {
        map_view_summary.visibility = View.GONE

        val title = intent.getStringExtra("TITLE")?:"강의실"

        if (title.length > 13)
            tv_title_summary.text = title.slice(0..13)+"∙∙∙"
        else
            tv_title_summary.text = title

        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

        if (wifiManager != null) {
            if (!wifiManager!!.isWifiEnabled) {
                wifiManager!!.isWifiEnabled = true
            }
        }
    }

    private fun setRoute(x: Int, y: Int, z: Int) {
        setMapView(z, y)
        when (z) {
            1 -> fl_1_summary.performClick()
            2 -> fl_2_summary.performClick()
            3 -> fl_3_summary.performClick()
            4 -> fl_4_summary.performClick()
        }
        // TODO change
//        networkRoute(x, y, z)
        networkRoute(18, 5, 3)
    }

    private fun setMapView(initF: Int, y: Int) {
        var tmp = 3
        when (initF) {
            1 -> tmp = R.drawable.f1
            2 -> tmp = R.drawable.f2
            3 -> tmp = R.drawable.f3
            4 -> tmp = R.drawable.f4
        }
        mapView = InnerMapView(this, BitmapFactory.decodeResource(resources, tmp), sv_vertical_summary)
        prt_summary.addView(mapView)
    }

    private fun networkRoute(x: Int, y: Int, z: Int) {
        val postRoute = networkService.postRoute(PostRouteData(x, y, z,
                intent.getIntExtra("X", 18),
                intent.getIntExtra("Y", 33),
                intent.getIntExtra("Z", 3))
        )

        postRoute.enqueue(object : Callback<PostRouteResponse> {
            override fun onFailure(call: Call<PostRouteResponse>?, t: Throwable?) {
                Log.d("ROUTE_FAIL", t.toString())
            }

            override fun onResponse(call: Call<PostRouteResponse>?, response: Response<PostRouteResponse>?) {
                if(response!!.isSuccessful){
                    // TODO 거리 구하기. 계단은 3m
                    setTextUI(10)
                    setListener()
                    responseToRoute(response.body().data)
                    mapView.route = mRoute[lastFloor]?:mRoute[3]!!

                } else{
                    Log.d("ROUTE_UNSUCCESSFUL", response.message())
                }
            }
        })
    }

    private fun setTextUI(distance: Int) {
        tv_time_summary.text = "${(distance/1.389).toInt()}초"
        tv_distance_summary.text = "${distance}m"
    }

    private fun setListener() {
        btn_start_summary.setOnClickListener {
            Intent(this, IndoorNaviActivity::class.java).let{
                // TODO intent route info
                startActivity(it)
                finish()
            }
        }
    }

    fun lastChange(f: Int) {
        changeMap(f)
        when (lastFloor) {
            1 -> fl_1_summary.changeBg()
            2 -> fl_2_summary.changeBg()
            3 -> fl_3_summary.changeBg()
            4 -> fl_4_summary.changeBg()
        }
        lastFloor = f
    }

    private fun changeMap(f: Int) {
        var imgId = 3

        when (f) {
            1 -> imgId = R.drawable.f1
            2 -> imgId = R.drawable.f2
            3 -> imgId = R.drawable.f3
            4 -> imgId = R.drawable.f4
        }

        mapView.img = BitmapFactory.decodeResource(resources, imgId)
        mapView.route = mRoute[f]?: floatArrayOf()
    }

    private fun initWIFIScan() {
        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(mWifiScanReceiver, filter)
        wifiManager!!.startScan()
    }
    /* 통신 */
    private fun networkCoord(rssi: ArrayList<PostCoordData>) {
        val postCoord = networkService.postCoord(rssi)

        postCoord.enqueue(object : Callback<PostCoordResponse> {
            override fun onFailure(call: Call<PostCoordResponse>?, t: Throwable?) {
                Log.d("COORD_FAIL", t.toString())
                // TODO DELETE
                setRoute(18, 5, 3)
            }

            override fun onResponse(call: Call<PostCoordResponse>?, response: Response<PostCoordResponse>?) {
                if (response!!.isSuccessful) {
                    mapView.responseCoord = response
                    // TODO 서버에서 z축 값도 줘야함
                    setRoute(response.body().data.x, response.body().data.y, 3)
                } else {
                    Log.d("COORD_UNSUCCESSFUL", response.message())
                }
            }
        })
    }

    private fun responseToRoute(res: ArrayList<PostRouteResponseData>) {
        var tmpFloor = res[0].z
        var tmpArr = ArrayList<Float>()

        // only first element puts once, others put twice
        tmpArr.add((res[0].x*40).toFloat())
        tmpArr.add(((105-res[0].y)*40).toFloat())
        res.removeAt(0)

        res.forEach {
            if (tmpFloor!=it.z) {
                if (tmpArr.size!=0) {
                    tmpArr.removeAt(tmpArr.size-1)
                    mRoute[tmpFloor] = tmpArr.toFloatArray()
                    tmpArr = ArrayList()
                    tmpArr.add((it.x*40).toFloat())
                    tmpArr.add(((105-it.y)*40).toFloat())
                }
                tmpFloor = it.z
            }
            tmpArr.add((it.x*40).toFloat())
            tmpArr.add(((105-it.y)*40).toFloat())
            tmpArr.add((it.x*40).toFloat())
            tmpArr.add(((105-it.y)*40).toFloat())
        }
        tmpArr.removeAt(tmpArr.size-1)
        mRoute[tmpFloor] = tmpArr.toFloatArray()
        Log.d("ASDFF", mRoute.toString())
    }


    /* WIFI Scan 값 관리 */
    fun getWIFIScanResult() {
        scanResult = wifiManager!!.scanResults

        accessPoints.clear()

        for (i in scanResult.indices) {
            val result = scanResult[i]
            accessPoints.add(
                AccessPoint(
                    result.SSID,
                    result.BSSID,
                    result.level.toDouble()
                )
            )
        }

        // AP BSSID 중복 제거
        accessPoints = rmOverlap(accessPoints)

        var postRssiData = ArrayList<PostCoordData>()
        accessPoints.forEach {
            postRssiData.add(PostCoordData(it.bssid, it.rssi))
        }
        unregisterReceiver(mWifiScanReceiver)
        networkCoord(postRssiData)
    }

    private fun rmOverlap(ap: ArrayList<AccessPoint>): ArrayList<AccessPoint> {
        var bssidSet = mutableSetOf<String>()
        var res = arrayListOf<AccessPoint>()

        ap.onEach {
            if (!bssidSet.contains(it.bssid)) {
                bssidSet.add(it.bssid)
                res.add(it)
            }
        }
        return res
    }

    /* Location permission 을 위한 메서드들 */
    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded = ArrayList<String>()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this@IndoorSummaryActivity, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this@IndoorSummaryActivity,
                listPermissionsNeeded.toTypedArray(),
                MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted")
                }
            }
        }
    }
    companion object {
        /* Location permission 을 위한 필드 */
        const val MULTIPLE_PERMISSIONS = 10 // code you want.
    }
}
