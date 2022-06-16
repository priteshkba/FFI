package com.ffi.wishlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_my_order.view.*

class OrderHolder(itemview: View) :
    RecyclerView.ViewHolder(itemview) {

    val li_order = itemview.li_order

    val tvPlaceOn = itemview.tvPlaceOn
    val tvOrderId = itemview.tvOrderId
    val tvDelivery_status = itemview.tvDelivery_status
    val tvAmnt = itemview.tvAmnt
    val tvcust_name = itemview.tvCustName
    val ResellerName=itemview.tvResellername

    val tvView=itemView.tvView


}
