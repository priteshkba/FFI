package com.ffi.category

import com.google.gson.annotations.SerializedName

data class Record(
    @field:SerializedName("Description")
    var Description: String = "",

    @field:SerializedName("ID")
    var ID: String = "",

    @field:SerializedName("Media")
    var Media: String = "",

    @field:SerializedName("Name")
    var Name: String = "",

    @field:SerializedName("vietype")
    var vietype: Int = 0,

    @field:SerializedName("isSelected")
    var isSelected: Boolean = false
)