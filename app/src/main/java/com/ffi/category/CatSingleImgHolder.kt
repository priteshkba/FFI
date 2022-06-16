package com.ffi.category

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_single_image_category.view.*

class CatSingleImgHolder(itemview:View): RecyclerView.ViewHolder(itemview) {
    val tvcategoryname=itemview.tvCategory
    val ivCategoryImg=itemview.ivCatbg
    val cv_img = itemview.cv_img
    val fi_main=itemview.fi_main
}
