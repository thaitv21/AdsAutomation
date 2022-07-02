package com.anythingl.adsautomation

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AppClient {

    companion object {
        private lateinit var instance: AppClient

        fun getInstance(): AppClient {
            if (!this::instance.isInitialized) {
                this.instance = AppClient();
            }
            return this.instance
        }
    }

    val appService: AppService

    init {
        val builder = Retrofit.Builder()
        builder.baseUrl("https://sieuvip.xyz/")
        builder.addConverterFactory(GsonConverterFactory.create())
        builder.client(OkHttpClient.Builder().build())
        val retrofit = builder.build()
        appService = retrofit.create(AppService::class.java)
    }
}