package com.ffi.catalog

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class Data(
    @field:SerializedName("TotalPage")
    var TotalPage: Int = 0,

    @field:SerializedName("records")
    var records: ArrayList<Record> = ArrayList()
)