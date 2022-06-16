package com.ffi.home


import com.ffi.login.BasicUrls
import com.google.gson.annotations.SerializedName

data class ResponseBasicUrl(
    @SerializedName("data")
    var `data`: BasicUrls = BasicUrls(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)