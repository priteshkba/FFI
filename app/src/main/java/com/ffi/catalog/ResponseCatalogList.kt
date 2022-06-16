package com.ffi.catalog

import com.google.gson.annotations.SerializedName

data class ResponseCatalogList(
    @field:SerializedName("data")
    var `data`: Data = Data(),

    @field:SerializedName("message")
    var message: String = "",

    @field:SerializedName("status")
    var status: Int = 0
)