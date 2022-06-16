package com.ffi.productdetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VariationList(

	@SerializedName("ID")
	@Expose
	var iD: Int = 0,

	@SerializedName("Name")
	@Expose
	var name: String? = "",

	@SerializedName("List")
	@Expose
	var list: List<Varient>? = ArrayList()


) {
    override fun toString(): String {
        return "VariationList(iD=$iD, name='$name', list=$list)"
    }
}