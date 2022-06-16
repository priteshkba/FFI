package com.ffi.payment


import com.ffi.add_address.ParamAddAddress
import com.ffi.add_address.ParamCopyAddress
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.File

data class ParamPayment(
    @SerializedName("paymentType")
    var paymentType: Int? = 0,

    @SerializedName("payWithWallet")
    var payWithWallet: Boolean? = false,

    @SerializedName("addressType")
    var addressType: Int? = 0,

    @SerializedName("addressData")
    var addressData: ParamAddAddress? = ParamAddAddress(),

    @SerializedName("transactionImage")
    var transactionImage: File? = File("")
)