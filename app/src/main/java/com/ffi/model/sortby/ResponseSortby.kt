package com.ffi.model.sortby


import com.ffi.collectiondetail.Data
import com.google.gson.annotations.SerializedName

data class ResponseSortby(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)