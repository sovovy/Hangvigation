package com.mobal.hangvigation.network

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplicationController: Application() {
    lateinit var networkService: NetworkService
    private val baseUrl = "http://15.164.103.226:3000/"
    companion object {
        lateinit var instance : ApplicationController
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        buildNetwork()
    }

    fun buildNetwork() {
        val builder = Retrofit.Builder()
        var retrofit = builder
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())//GSON을 JSON으로 쓸수있도록
            .build()  //베이스 유알엘을 가지고 통신을 할것

        networkService = retrofit.create(NetworkService::class.java)
    }
}