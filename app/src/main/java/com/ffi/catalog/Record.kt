package com.ffi.catalog

import com.ffi.productlist.Media
import com.google.gson.annotations.SerializedName

data class Record(
    @field:SerializedName("Description")
    var Description: String = "",

    @field:SerializedName("ID")
    var ID: String = "",

    @field:SerializedName("Media")
    var Media: List<Media> = listOf(),

    @field:SerializedName("Name")
    var Name: String = ""
)