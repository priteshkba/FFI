package com.ffi.my_orderdetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProductReturnReasonResponse {
    @Expose
    @SerializedName("data")
    val data: List<DataEntity>? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("status")
    val status = 0

    class DataEntity {
        @Expose
        @SerializedName("ReasonText")
        val reasontext: String? = null

        @Expose
        @SerializedName("ID")
        val id: String? = null
    }
}