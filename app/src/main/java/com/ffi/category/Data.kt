package com.ffi.category

import com.google.gson.annotations.SerializedName

data class Data(
    @field:SerializedName("TotalPage")
    var TotalPage: Int = 0,

    @field:SerializedName("records")
    var records: ArrayList<Record> = ArrayList()
)