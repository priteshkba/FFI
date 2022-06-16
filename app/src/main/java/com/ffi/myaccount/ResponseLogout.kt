package com.ffi.myaccount

import com.google.gson.annotations.SerializedName

data class ResponseLogout(
    @field:SerializedName("data")
    var `data`: List<Any> = listOf(),

    @field:SerializedName("message")
    var message: String = "",

    @field:SerializedName("status")
    var status: Int = 0
)