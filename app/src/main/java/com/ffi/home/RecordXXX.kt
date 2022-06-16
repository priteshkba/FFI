package com.ffi.home


import com.google.gson.annotations.SerializedName

data class RecordXXX(
    @SerializedName("Description")
    var description: String = "",
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("Media")
    var media: List<Media> = listOf(),
    @SerializedName("MediaFolder")
    var mediaFolder: String = "",
    @SerializedName("Price")
    var price: String = "",
    @SerializedName("SKU")
    var sKU: String = "",
    @SerializedName("Title")
    var title: String = ""
)