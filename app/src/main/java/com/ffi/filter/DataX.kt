package com.ffi.filter


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("VariantValue")
    var variantValue: String = "",
    @SerializedName("VariantValueId")
    var variantValueId: String = "",
    @SerializedName("isSelected")
    var isSelected:Boolean=false
)