package com.ffi.home

import com.google.gson.annotations.SerializedName

data class PopularCatalogue(
    @field:SerializedName("Records")
    var Records: List<Record> = listOf(),

    @field:SerializedName("Title")
    var Title: String = ""
)