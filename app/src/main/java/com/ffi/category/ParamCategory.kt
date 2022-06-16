package com.ffi.category

import com.google.gson.annotations.SerializedName

class ParamCategory {
    @field:SerializedName("page")
    var page: String = ""

    @field:SerializedName("limit")
    var limit: String = ""
    override fun toString(): String {
        return "ParamCategory(page='$page', limit='$limit')"
    }


}
