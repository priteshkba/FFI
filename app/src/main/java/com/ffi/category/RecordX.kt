package com.ffi.category


import com.google.gson.annotations.SerializedName

data class RecordX(
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("Media")
    var media: String = "",
    @SerializedName("Name")
    var name: String = "",
    @field:SerializedName("vietype")
    var vietype: Int = 0
)