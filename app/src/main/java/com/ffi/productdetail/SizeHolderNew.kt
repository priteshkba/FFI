package com.ffi.productdetail

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_size2.view.*

class SizeHolderNew(itemview:View): RecyclerView.ViewHolder(itemview) {
    val tvSize=itemview.tvSize
    val ivDivideLine=itemview.ivDivideLine
    val li_main_layout=itemview.li_main_layout
    val li_view_selected =itemview.li_view_selected
    val iv_view_selected =itemview.rl_view_selected
    val li_color=itemview.li_selected
    //val fi_color=itemview.fi_color
}
