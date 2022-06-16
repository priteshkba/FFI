package com.ffi.login

import com.google.gson.annotations.SerializedName

data class BasicUrls(
    @field:SerializedName("MediaUrl")
    var MediaUrl: String? = "",

    @field:SerializedName("SiteUrl")
    var SiteUrl: String = "",

    @field:SerializedName("PaymentUrl")
    var PaymentUrl: String = "",

    @field:SerializedName("PaymentSuccessUrl")
    var PaymentSuccessUrl: String = "",

    @field:SerializedName("PaymentCancelUrl")
    var PaymentCancelUrl: String = "",

    @field:SerializedName("TermsCondition")
    var TermsCondition: String = "",
    @field:SerializedName("PrivacyPolicy")
    var PrivacyPolicy: String = "",
    @field:SerializedName("CancellationPolicy")
    var CancellationPolicy: String = "",
    @field:SerializedName("AboutUs")
    var AboutUs: String = "",
    @field:SerializedName("ContactUs")
    var ContactUs: String = ""
)