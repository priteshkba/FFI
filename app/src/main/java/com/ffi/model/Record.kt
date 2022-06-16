package com.ffi.model


import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("Description")
    var description: String = "",
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("Media")
    var media: List<Media> = listOf(),
    @SerializedName("MediaFolder")
    var mediaFolder: String = "",
    @SerializedName("Name")
    var name: String = ""
)