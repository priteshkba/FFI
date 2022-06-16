package com.ffi.model


import com.google.gson.annotations.SerializedName


data class ParamGetSharedData(
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("page")
    var page: String = "",
    @SerializedName("typeId")
    var typeId: String = ""
) {
    override fun toString(): String {
        return "ParamGetSharedData(limit='$limit', page='$page', typeId='$typeId')"
    }
}

