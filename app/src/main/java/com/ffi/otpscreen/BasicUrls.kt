package com.ffi.otpscreen

import com.google.gson.annotations.SerializedName

data class BasicUrls(
    @field:SerializedName("MediaUrl")
    var MediaUrl: String = "",

    @field:SerializedName("SiteUrl")
    var SiteUrl: String = "",

    @field:SerializedName("PaymentUrl")
    var PaymentUrl: String = "",

    @field:SerializedName("PaymentSuccessUrl")
    var PaymentSuccessUrl: String = "",

    @field:SerializedName("PaymentCancelUrl")
    var PaymentCancelUrl: String = ""
)