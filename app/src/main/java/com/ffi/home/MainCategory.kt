package com.ffi.home

import com.google.gson.annotations.SerializedName

data class MainCategory(
    @field:SerializedName("TotalItems")
    var TotalItems: String = "",

    @field:SerializedName("ID")
    var ID: String = "",

    @field:SerializedName("Media")
    var Media: String = "",

    @field:SerializedName("Name")
    var Name: String = ""

)