package com.ffi.reviewlist


import com.google.gson.annotations.SerializedName

data class ParamReviews(
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("page")
    var page: String = "",
    @SerializedName("productId")
    var productId: String = ""
) {
    override fun toString(): String {
        return "ParamReviews(limit='$limit', page='$page', productId='$productId')"
    }
}