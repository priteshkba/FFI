package com.ffi.home


import com.google.gson.annotations.SerializedName

data class Media(
    @SerializedName("Media")
    var media: String = "",
    @SerializedName("MediaTypeId")
    var mediaTypeId: String = ""
)