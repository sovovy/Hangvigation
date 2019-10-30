package com.mobal.hangvigation

class AccessPoint(val ssid: String, val bssid: String, var rssi: Double, var sum: Double = rssi, var cnt: Int = 1){
    override fun toString(): String {
        return "**$bssid**"
    }
}