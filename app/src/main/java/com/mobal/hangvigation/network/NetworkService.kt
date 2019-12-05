package com.mobal.hangvigation.network

import com.mobal.hangvigation.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkService {
    @POST("/coord")
    fun postCoord(@Body rssi: ArrayList<PostCoordData>): Call<PostCoordResponse>

    @POST("/route")
    fun postRoute(@Body coords: PostRouteData): Call<PostRouteResponse>

    @GET("/indoor/division/{divisionIdx}")
    fun getDivision(@Path("divisionIdx") divisionIdx: Int): Call<GetDivisionResponse>
}