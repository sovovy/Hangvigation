package com.mobal.hangvigation.network

import com.mobal.hangvigation.model.*
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {
    @POST("/coord")
    fun postCoord(@Body rssi: ArrayList<PostCoordData>): Call<PostCoordResponse>

    @POST("/route")
    fun postRoute(@Body coords: PostRouteData): Call<PostRouteResponse>

    @POST("/outdoor")
    fun postOutdoor(@Body outdoor : PostOutdoorData): Call<PostOutdoorResponse>

    @GET("/indoor/division/{divisionIdx}")
    fun getDivision(@Path("divisionIdx") divisionIdx: Int): Call<GetDivisionResponse>

    @GET("/indoor/search")
    fun getSearch(@Query("q") query: String): Call<GetDivisionResponse>

    @GET("/indoor/info/{indoorPlaceIdx}")
    fun getInfo(@Path("indoorPlaceIdx") indoorPlaceIdx: Int): Call<GetInfoResponse>
}