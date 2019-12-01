package com.mobal.hangvigation.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkService {
    @POST("/coord") // bssid, rssi 리스트 가져오기
    fun postCoord(@Body rssi: ArrayList<PostCoordData>): Call<PostCoordResponse>
}