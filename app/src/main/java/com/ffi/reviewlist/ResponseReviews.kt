package com.ffi.reviewlist


import com.google.gson.annotations.SerializedName

data class ResponseReviews(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)