package com.ffi.collectiondetail


import com.google.gson.annotations.SerializedName

data class ResponseCollectionDetails(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)