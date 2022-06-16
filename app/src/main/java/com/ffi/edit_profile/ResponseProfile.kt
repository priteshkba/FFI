package com.ffi.edit_profile


import com.google.gson.annotations.SerializedName

data class ResponseProfile(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)