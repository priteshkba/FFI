package com.ffi.collectiondetail


import com.google.gson.annotations.SerializedName

data class ParamCollectionDetail(
    @SerializedName("catalogueId")
    var catalogueId: String = "",
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("page")
    var page: String = ""
) {
    override fun toString(): String {
        return "ParamCollectionDetail(catalogueId=$catalogueId, limit=$limit, page='$page')"
    }
}