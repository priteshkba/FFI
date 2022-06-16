package com.ffi.home

import com.google.gson.annotations.SerializedName

data class ResponseHome(
    @field:SerializedName("data")
    var data: Data = Data(),

    @field:SerializedName("message")
    var message: String = "",

    @field:SerializedName("status")
    var status: Int = 0
)