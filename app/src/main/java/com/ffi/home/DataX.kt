package com.ffi.home


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("records")
    var records: List<RecordXXX> = listOf(),
    @SerializedName("TotalPage")
    var totalPage: Int = 0
)