package com.ffi.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_populer_category.view.*

class PopluerCategoryHolder(itemview:View): RecyclerView.ViewHolder(itemview) {
    val tvcategoryname=itemview.tvpop_category
    val ivCategoryImg=itemview.ivCatalog
    val li_maincategory=itemview.li_maincategory

}
