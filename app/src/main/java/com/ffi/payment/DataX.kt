package com.ffi.payment


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("DisplayName")
    var displayName: String? = "",
    @SerializedName("Value")
    var value: String? = "",
    @SerializedName("ID")
    var id: String? = ""
)