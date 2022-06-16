package com.ffi.add_address


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("StateName")
    var stateName: String = ""
)