package com.ffi.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_top_50.view.*

class Top50Holder(itemview:View): RecyclerView.ViewHolder(itemview) {
    val tvitemname=itemview.tvItems
    val tvItemsPrice=itemview.tvPrice
    val ivitemImg=itemview.ivitem
    val litopitems=itemview.litopitems
    val ivFav=itemview.ivFav
    val ivitem=itemview.ivitem

}
