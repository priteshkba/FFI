package com.ffi.productlist

import com.google.gson.annotations.SerializedName

class ParamProductList {

    @field:SerializedName("categoryId")
    var categoryId: String = ""

    @field:SerializedName("page")
    var page: String = ""

    @field:SerializedName("limit")
    var limit: String = ""

    override fun toString(): String {
        return "ParamProductList(categoryId='$categoryId', page='$page', limit='$limit')"
    }


}
