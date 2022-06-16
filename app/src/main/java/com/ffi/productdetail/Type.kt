package com.ffi.productdetail

import com.google.gson.annotations.SerializedName

data class Type(

    @field:SerializedName("VariantId")
    var VariantId: Int = -1,

    @field:SerializedName("VariantName")
    var VariantName: String = "",

    @field:SerializedName("VariantValue")
    var VariantValue: String = "",

    @field:SerializedName("VariantCode")
    var VariantCode : String = ""
)