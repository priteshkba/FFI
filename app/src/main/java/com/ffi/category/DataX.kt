package com.ffi.category


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("records")
    var records: List<Record> = listOf(),
    @SerializedName("TotalPage")
    var totalPage: Int = 0
)