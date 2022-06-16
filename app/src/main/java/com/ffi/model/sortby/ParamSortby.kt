package com.ffi.model.sortby


import com.google.gson.annotations.SerializedName

data class ParamSortby(
    @SerializedName("categoryId")
    var categoryId: String = "",
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("page")
    var page: String = "",
    @SerializedName("sortTypeId")
    var sortTypeId: String = "",
    @SerializedName("catalogueId")
    var catalogueId: String = "0",
    @SerializedName("typeId")
    var typeId: String = ""
) {
    override fun toString(): String {
        return "ParamSortby(categoryId='$categoryId', limit='$limit', page='$page', sortTypeId='$sortTypeId')"
    }
}