package com.ffi.productlist.topproduct


import com.ffi.productlist.Record
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("Records")
    var records: List<Record> = listOf(),
    @SerializedName("Title")
    var title: String = "",
    @SerializedName("TotalPage")
    var totalPage: Int = 0
)