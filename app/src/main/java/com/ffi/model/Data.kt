package com.ffi.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("records")
    var records: List<Record> = listOf(),
    @SerializedName("TotalPage")
    var totalPage: Int = 0
)