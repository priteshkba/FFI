package com.ffi.filter


import com.google.gson.annotations.SerializedName

data class ParamFilterResult(
    @SerializedName("categoryId")
    var categoryId: String = "",
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("page")
    var page: String = "",
    @SerializedName("variantIds")
    var variantIds: String = ""


) {
    override fun toString(): String {
        return "ParamFilterResult(categoryId='$categoryId', limit='$limit', page='$page', variantIds='$variantIds')"
    }
}