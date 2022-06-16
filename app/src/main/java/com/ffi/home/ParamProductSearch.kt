package com.ffi.home


import com.google.gson.annotations.SerializedName

data class ParamProductSearch(
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("page")
    var page: String = ""
) {
    override fun toString(): String {
        return "ParamProductSearch(limit=$limit, name='$name', page='$page')"
    }
}