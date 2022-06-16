package com.ffi.filter


import com.google.gson.annotations.SerializedName

data class DataXX(
    @SerializedName("records")
    var records: List<Record> = listOf(),
    @SerializedName("TotalPage")
    var totalPage: Int = 0
)