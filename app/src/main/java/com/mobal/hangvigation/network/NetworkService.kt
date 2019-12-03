package com.mobal.hangvigation.network

import com.mobal.hangvigation.model.PostCoordData
import com.mobal.hangvigation.model.PostCoordResponse
import com.mobal.hangvigation.model.PostRouteData
import com.mobal.hangvigation.model.PostRouteResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkService {
    @POST("/coord")
    fun postCoord(@Body rssi: ArrayList<PostCoordData>): Call<PostCoordResponse>

    @POST("/route")
    fun postRoute(@Body coords: PostRouteData): Call<PostRouteResponse>
}