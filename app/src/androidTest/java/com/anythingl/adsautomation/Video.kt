package com.anythingl.adsautomation

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("VideoIds")
    val videoId: String,
    @SerializedName("CountClick")
    val countClick: Int,
    @SerializedName("TimeClick")
    val timeClick: Int,
    var duration: Int = 60,
)
