package com.ffi.add_address


import com.google.gson.annotations.SerializedName

data class ParamStateList(
    @SerializedName("countryId")
    var countryId: String = ""
)