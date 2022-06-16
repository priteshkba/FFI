package com.ffi.productlist.topproduct


import com.google.gson.annotations.SerializedName

data class ParamGetProduct(
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("page")
    var page: String = ""
)