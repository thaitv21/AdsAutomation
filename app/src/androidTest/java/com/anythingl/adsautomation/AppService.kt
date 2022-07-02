package com.anythingl.adsautomation

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header


interface AppService {
    @GET("ClickAds/GetRun")
    fun getVideoAds(): Call<Video>

}