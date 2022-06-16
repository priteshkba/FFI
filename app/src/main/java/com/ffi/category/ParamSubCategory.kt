package com.ffi.category

import com.google.gson.annotations.SerializedName

class ParamSubCategory {
    @field:SerializedName("categoryId")

    var categoryId: String = ""

    @field:SerializedName("page")
    var page: String = ""

    @field:SerializedName("limit")
    var limit: String = ""

    override fun toString(): String {
        return "ParamSubCategory(categoryId='$categoryId', page='$page', limit='$limit')"
    }


}
