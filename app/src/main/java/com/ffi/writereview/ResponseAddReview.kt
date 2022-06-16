package com.ffi.writereview


import com.google.gson.annotations.SerializedName

data class ResponseAddReview(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)