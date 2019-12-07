package com.mobal.hangvigation.ui.indoor_navi

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
import com.mobal.hangvigation.R
import com.mobal.hangvigation.model.*
import com.mobal.hangvigation.network.ApplicationController
import com.mobal.hangvigation.network.NetworkService
import kotlinx.android.synthetic.main.activity_indoor_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalArgumentException

class IndoorNaviActivity : AppCompatActivity() {
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

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                finish()
            }
        }
        initWIFIScan()
    }

    override fun onRestart() {
        super.onRestart()
        initWIFIScan()
    }

    override fun onResume() {
        super.onResume()
        initWIFIScan()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mWifiScanReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(mWifiScanReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_indoor_map)

        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

        if (wifiManager != null) {
            if (!wifiManager!!.isWifiEnabled) {
                wifiManager!!.isWifiEnabled = true
            }
            val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            registerReceiver(mWifiScanReceiver, filter)
            wifiManager!!.startScan()
        }

        // 네트워크
        networkService = ApplicationController.instance.networkService

        // route communication
        setRoute()
    }

    fun lastChange(f: Int) {
        changeMap(f)
        when (lastFloor) {
            1 -> fl_1.changeBg()
            2 -> fl_2.changeBg()
            3 -> fl_3.changeBg()
            4 -> fl_4.changeBg()
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

            }

            override fun onResponse(call: Call<PostCoordResponse>?, response: Response<PostCoordResponse>?) {
                if (response!!.isSuccessful) {
                    mapView.responseCoord = response
                } else {
                    Log.d("COORD_UNSUCCESSFUL", response.message())
                }
            }
        })
    }

    private fun setRoute() {
        val route = intent.getParcelableArrayListExtra<PostRouteResponseData>("ROUTE")
        responseToRoute(route)

        var floorImg = arrayOf(R.drawable.f1, R.drawable.f2, R.drawable.f3, R.drawable.f4)
        mapView = InnerMapView(this, BitmapFactory.decodeResource(resources, floorImg[route[0].z-1]), sv_vertical)
        prt.addView(mapView)

        // set mapView route
        mapView.route = mRoute[route[0].z]?:mRoute[4]!!

        // current location's floor
        when (route[0].z) {
            1 -> fl_1.performClick()
            2 -> fl_2.performClick()
            3 -> fl_3.performClick()
            4 -> fl_4.performClick()
        }

        btn_location.setOnClickListener {
            mapView.moveScreen()
        }
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
            result = ContextCompat.checkSelfPermission(this@IndoorNaviActivity, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this@IndoorNaviActivity,
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
