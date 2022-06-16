package com.ffi.filter


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("data")
    var `data`: List<DataX> = listOf(),
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("Name")
    var name: String = "",
    @SerializedName("isSelected")
    var isSelected:Boolean=false
)