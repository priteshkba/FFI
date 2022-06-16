package com.ffi.home

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName

class CatalogModel {

    @field:SerializedName("CatlogName")
    var CatlogName: String = ""

    @field:SerializedName("CatalogImg")
    @DrawableRes
    var CatalogImg: Int = 0
}