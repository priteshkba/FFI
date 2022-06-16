package com.ffi.filter


import com.google.gson.annotations.SerializedName

data class Media(
    @SerializedName("Media")
    var media: String = "",
    @SerializedName("MediaTypeId")
    var mediaTypeId: String = ""
)