package com.ffi.model


import com.google.gson.annotations.SerializedName

data class ResponseAddSharedData(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)