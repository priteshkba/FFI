package com.ffi.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_category.view.*

class CategoryHolder(itemview:View): RecyclerView.ViewHolder(itemview) {
    val tvcategoryname=itemview.tvCategory
    val tvItems=itemview.tvnoItems
    val ivCategoryImg=itemview.ivbg
    val fi_main=itemview.fi_main
    val ivShade=itemview.ivshade

}
