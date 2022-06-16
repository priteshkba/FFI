package com.ffi.add_address


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("iso")
    var iso: String = "",
    @SerializedName("name")
    var name: String = ""
)