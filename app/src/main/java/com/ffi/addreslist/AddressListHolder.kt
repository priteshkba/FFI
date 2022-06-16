package com.ffi.addreslist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_addresslist.view.*

class AddressListHolder(itemview: View) :
    RecyclerView.ViewHolder(itemview) {

    val tvaddressNo = itemview.tvAddno
    val li_address = itemview.li_address
    val tvAddress=itemview.tvAddress
    val ivDelete=itemview.ivDelete
    val ivSelected=itemview.ivSelected


}
