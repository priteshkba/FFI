package com.ffi.productlist

import com.google.gson.annotations.SerializedName

data class Media(
    @field:SerializedName("Media")
    var Media: String = "",

    @field:SerializedName("MediaTypeId")
    var MediaTypeId: String = ""
)