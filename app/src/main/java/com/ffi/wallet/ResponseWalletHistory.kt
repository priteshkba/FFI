package com.ffi.wallet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseWalletHistory {
    @Expose
    @SerializedName("transactionDetail")
    val transactiondetail: List<TransactiondetailEntity>? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("status")
    val status = 0

    @Expose
    @SerializedName("TotalPage")
    val TotalPage = 0

    class TransactiondetailEntity {

        @Expose
        @SerializedName("Amount")
        val amount: String? = null


        @Expose
        @SerializedName("description")
        val description: String? = null

        @Expose
        @SerializedName("TransactionDate")
        val transactiondate: String? = null

        @Expose
        @SerializedName("TransactionId")
        val TransactionId: String? = null

        @Expose
        @SerializedName("CreditDebit")
        val CreditDebit: String? = null
    }
}