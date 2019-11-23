package com.mobal.hangvigation

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
import kotlinx.android.synthetic.main.activity_indoor_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.pow

class IndoorMapActivity : AppCompatActivity() {
    private var accessPoints: ArrayList<AccessPoint> = ArrayList()
    private var accessPointsStack: ArrayList<AccessPoint> = ArrayList()
    private lateinit var networkService: NetworkService
    private var wifiManager: WifiManager? = null
    private lateinit var scanResult: List<ScanResult>
    private var permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
    lateinit var mapView : InnerMapView
    var sum1 = 0.0f
    var cnt1 = 0
    var sum2 = 0.0f
    var cnt2 = 0

    private val mWifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null) {
                if (action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    Log.d("ASDF", "ASDASDAS")
                    getWIFIScanResult()
                    wifiManager!!.reconnect()
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
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(mWifiScanReceiver)
//    }

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

        mapView = InnerMapView(this, BitmapFactory.decodeResource(resources, R.drawable.f3), sv_vertical)
        prt.addView(mapView)

        // 네트워크
        networkService = ApplicationController.instance.networkService

    }

    /* 통신 */
    private fun network(rssi: ArrayList<PostCoordData>){
        val postCoord = networkService.postCoord(rssi)

        postCoord.enqueue(object : Callback<PostCoordResponse> {
            override fun onFailure(call: Call<PostCoordResponse>?, t: Throwable?) {
                Log.d("ASDFF", "${t.toString()}")

            }

            override fun onResponse(call: Call<PostCoordResponse>?, response: Response<PostCoordResponse>?) {
                if(response!!.isSuccessful){
                    mapView.response = response
                    val x = response.body().data.x
                    val y = response.body().data.y
                    // 예전 방식
                    val x2 = response.body().data.x2
                    val y2 = response.body().data.y2
                    val x3 = 10
                    val y3 = 17
                    sum1 += getDistance(x, y, x3, y3)
                    cnt1++
                    sum2 += getDistance(x2, y2, x3, y3)
                    cnt2++
                    Log.d("ASDFF", "11111 ${response.body().data.x}, ${response.body().data.y} 오차 거리: ${sum1/cnt1}")
                    Log.d("ASDFF", "22222 ${response.body().data.x2}, ${response.body().data.y2} 오차 거리: ${sum2/cnt2}")
                    mapView.x = response.body().data.x
                    mapView.y = response.body().data.y
                } else{
                    Log.d("ASDFF", "${response.message()}")
                }
            }

            private fun getDistance(x: Int, y: Int, i: Int, i1: Int): Float {
                return ((x-i).toFloat().pow(2) + (y-i1).toFloat().pow(2)).pow(0.5f)
            }
        })
    }

    /* WIFI Scan 값 관리 */
    fun getWIFIScanResult() {
        scanResult = wifiManager!!.scanResults

        accessPoints.clear()

        for (i in scanResult.indices) {
            val result = scanResult[i]
            accessPoints.add(AccessPoint(result.SSID, result.BSSID, result.level.toDouble()))
        }

        // AP BSSID 중복 제거
        accessPoints = rmOverlap(accessPoints)

        // APStack에 쌓기
        accessPointsStack = if (accessPointsStack.isEmpty()) {
            accessPoints
        } else {
            addToStack(accessPoints, accessPointsStack)
        }


        // 가장 높은 cnt가 5보다 크면 통신
        accessPointsStack = ArrayList(accessPointsStack.sortedWith(compareBy { it.cnt }).reversed())
        var postRssiData = ArrayList<PostCoordData>()

        if (accessPointsStack[0].cnt > 5) {
            accessPointsStack.forEach {
                postRssiData.add(PostCoordData(it.bssid, it.rssi))
            }

            network(postRssiData)

            // 다음 위치 추정을 위해 stack 초기화
            accessPointsStack.clear()
        }
    }

    private fun addToStack(new: ArrayList<AccessPoint>, stack: ArrayList<AccessPoint>): ArrayList<AccessPoint> {
        var res:ArrayList<AccessPoint> = stack

        new.onEach {
            // stack에 동일한 BSSID가 있다면 해당 stack의 idx 반환
            val idx = alreadyBssid(it.bssid, stack)
            if (idx!=-1) {
                res[idx].rssi = it.rssi
                res[idx].cnt++
                res[idx].sum += it.rssi
            }
        }

        return res
    }

    private fun alreadyBssid(bssid: String, stack: ArrayList<AccessPoint>): Int {
        // stack에 동일한 BSSID가 있다면 해당 stack의 idx 반환
        for (i in stack.indices) {
            if (stack[i].bssid == bssid) {
                return i
            }
        }
        return -1
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
            result = ContextCompat.checkSelfPermission(this@IndoorMapActivity, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this@IndoorMapActivity,
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
