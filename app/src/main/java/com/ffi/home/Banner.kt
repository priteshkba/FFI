package com.ffi.home

import com.google.gson.annotations.SerializedName

data class Banner(
    @field:SerializedName("Description")
    var Description: String = "",

    @field:SerializedName("HttpMethod")
    var HttpMethod: String = "",

    @field:SerializedName("Link")
    var Link: String = "",

    @field:SerializedName("LinkParams")
    var LinkParams: List<LinkParam> = listOf(),

    @field:SerializedName("Media")
    var Media: String = "",

    @field:SerializedName("Title")
    var Title: String = ""
)