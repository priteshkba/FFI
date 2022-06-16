package com.ffi.category

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_double_img_category.view.*

class CatDoubleImgHolder(itemview:View): RecyclerView.ViewHolder(itemview) {
    val tvcategoryname=itemview.tvCategory1
    val ivCategoryImg=itemview.ivCatbg1

    val tvcategoryname1=itemview.tvCategory2
    val ivCategoryImg1=itemview.ivCatb2

    val li_main=itemview.li_main

}
