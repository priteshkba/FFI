package com.ffi.home

import com.google.gson.annotations.SerializedName

data class TopProduct(
    @field:SerializedName("Records")
    var Records: List<RecordXX> = listOf(),

    @field:SerializedName("Title")
    var Title: String = ""
)