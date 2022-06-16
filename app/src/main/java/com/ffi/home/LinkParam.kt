package com.ffi.home

import com.google.gson.annotations.SerializedName

data class LinkParam(
    @field:SerializedName("ParamValue")
    var ParamValue: String = "",

    @field:SerializedName("ParameterName")
    var ParameterName: String = "",

    @field:SerializedName("ParentCategoryId")
    var ParentCategoryId : Int = 0
)