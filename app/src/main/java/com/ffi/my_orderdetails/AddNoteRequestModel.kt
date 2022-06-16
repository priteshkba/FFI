package com.ffi.my_orderdetails

import com.google.gson.annotations.SerializedName

class AddNoteRequestModel(@SerializedName("orderId")
                          var orderId: String = ""
) {
}