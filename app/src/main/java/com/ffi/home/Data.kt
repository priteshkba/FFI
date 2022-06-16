package com.ffi.home

import com.google.gson.annotations.SerializedName

data class Data(
    @field:SerializedName("Banners")
    var Banners: ArrayList<Banner> = ArrayList(),

    @field:SerializedName("PopularCatalogue")
    var PopularCatalogue: PopularCatalogue? = PopularCatalogue(),

    @field:SerializedName("MainCategory")
    var MainCategory: List<MainCategory> = listOf(),

    @field:SerializedName("PopularCategory")
    var PopularCategory: PopularCategory? = PopularCategory(),

    @field:SerializedName("TopProduct")
    var TopProduct: TopProduct = TopProduct(),

    @field:SerializedName("CartItems")
    var CartItems: String = "",
    @field:SerializedName("VersionUpdate")
    var VersionUpdate: String = "",

    @field:SerializedName("GetCurrentMembership")
    var GetCurrentMembership: String = ""

)