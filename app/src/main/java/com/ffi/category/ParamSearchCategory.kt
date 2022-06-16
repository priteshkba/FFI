package com.ffi.category


import com.google.gson.annotations.SerializedName

data class ParamSearchCategory(
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("page")
    var page: String = ""
)