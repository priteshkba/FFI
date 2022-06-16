package com.ffi.home

import com.google.gson.annotations.SerializedName

data class PopularCategory(
    @field:SerializedName("Records")
    var Records: List<RecordX>? = listOf(),

    @field:SerializedName("Title")
    var Title: String? = ""
)