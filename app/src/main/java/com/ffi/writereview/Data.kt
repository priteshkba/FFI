package com.ffi.writereview


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("Rating")
    var rating: String = ""
)