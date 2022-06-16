package com.ffi.productdetail

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_size.view.*

class SizeHolder(itemview:View): RecyclerView.ViewHolder(itemview) {
    val tvSize=itemview.tvSize
    val li_main_layout=itemview.li_main_layout
    val li_color=itemview.li_selected
    //val fi_color=itemview.fi_color
}
