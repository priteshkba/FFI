package com.ffi.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @Expose
    @SerializedName("data")
    var data: Data=Data(),

    @Expose
    @SerializedName("message")
    var message: String="",

    @Expose
    @SerializedName("status")
    var status: Int=0
)