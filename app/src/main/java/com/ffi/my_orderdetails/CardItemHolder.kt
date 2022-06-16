package com.ffi.my_orderdetails

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_order.view.*


class CardItemHolder(itemview: View) :
    RecyclerView.ViewHolder(itemview) {

    val tvSize = itemview.tvSize
    val tvProductName = itemview.tvProductName
    val ivcatlog = itemview.ivProductImg
    val tvQty = itemview.tvQty
    val tvPrice = itemview.tvPrice
    val tvSeller = itemview.tvSeller
    val li_review = itemview.li_review
    val tvTreckingcode = itemview.tvTreckingcode
    val tvTreckingurl = itemview.tvTreckingurl
    val ivshare = itemview.ivshare
    val li_tracking = itemview.li_tracking
    val tvStatus = itemview.tvStatus
    val li_return_product = itemview.li_return_product
    val tvReturnedProduct = itemview.tvReturnedProduct
    val li_main = itemview.li_main

}
