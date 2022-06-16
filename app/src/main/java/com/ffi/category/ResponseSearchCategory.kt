package com.ffi.category


import com.google.gson.annotations.SerializedName

data class ResponseSearchCategory(
    @SerializedName("data")
    var `data`: DataX = DataX(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)