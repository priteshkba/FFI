package com.ffi.cart


import com.google.gson.annotations.SerializedName

data class Type(
    @SerializedName("VariantCode")
    var variantCode: String? = "",
    @SerializedName("VariantName")
    var variantName: String? = "",
    @SerializedName("VariantValue")
    var variantValue: String? = ""
)