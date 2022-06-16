package com.ffi.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Data(
    @Expose
    @SerializedName("otp")
    var otp: Int= 0
)